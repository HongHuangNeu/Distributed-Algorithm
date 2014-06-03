package assignment3;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


import assignment3.DelayedMessageSender;
import assignment1.Process;
import assignment1.clock.VectorClock;
import assignment1.clock.VectorTimeStamp;

//@SuppressWarnings("serial")
public class Component extends UnicastRemoteObject implements RMI,
        Runnable, Serializable {
    private static final long serialVersionUID = 7247714666080613254L;
    private int LN;
    private float FN;
    private State SN = State.Sleep;
    private int in_branch;
    private int test_edge = Accept.Initial;
    private int best_edge;
    private float best_weight;
    private int find_count;
    private float[] adjacent;
    private int c = 0;
    private boolean printed = false;
    Queue<Message> queue = new LinkedList<Message>();
    Queue<Message> messageLink = new LinkedList<Message>();

	/*
     * LN=0;
			SN=State.Found;
			find_count=0;
	 * */

    private ArrayList<Integer> unknown_inMST = new ArrayList<Integer>();
    private ArrayList<Integer> not_inMST = new ArrayList<Integer>();
    private ArrayList<Integer> inMST = new ArrayList<Integer>();

    private int componentId;
    private int totalNumber;
    private int maxDelay = 1000;
    private int[] msgIndex;
    private int[] expectIndex;

    public Component(int componentIndex, int totalNumber, float[] adjacent)
            throws RemoteException {
        super();
        this.componentId = componentIndex;
        this.totalNumber = totalNumber;
        this.adjacent = new float[totalNumber];
        msgIndex = new int[totalNumber];
        expectIndex = new int[totalNumber];
        for (int i = 0; i < msgIndex.length; i++) {
            msgIndex[i] = 0;
            expectIndex[i] = 0;
        }
        for (int i = 0; i < adjacent.length; i++) {
            this.adjacent[i] = adjacent[i];
        }
        for (int i = 0; i < this.adjacent.length; i++) {

            if (this.adjacent[i] != Float.MAX_VALUE) {
                this.unknown_inMST.add(i);
            }
        }

        try {
            Registry registry = LocateRegistry.getRegistry(4303);
            registry.rebind(Integer.toString(this.componentId), this);
            System.out.println("bind successful");
        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    public void processMessage(Message message) {

        if (message instanceof Connect) {
            this.processConnect((Connect) message);
            //		processRequest((Request)message);
        }
        if (message instanceof ChangeRoot) {
            this.processChangeRoot();
            //		processRequest((Request)message);
        }
        if (message instanceof Accept) {
            this.processAccept((Accept) message);
            //		processRequest((Request)message);
        }
        if (message instanceof Initiate) {
            this.processInitial((Initiate) message);
            //		processRequest((Request)message);
        }
        if (message instanceof Reject) {
            this.processReject((Reject) message);
            //		processRequest((Request)message);
        }
        if (message instanceof Report) {
            this.processReport((Report) message);
            //		processRequest((Request)message);
        }
        if (message instanceof Test) {
            this.processTest((Test) message);
            //		processRequest((Request)message);
        }
        if (message instanceof CheckTerminate) {
            checkTerminate();
        }


    }


    public void send(Message m, int receiverIndex) {

        m.seq_id = msgIndex[receiverIndex];
        msgIndex[receiverIndex]++;
        try {

            // wrap message contents m into message object
            Message mObj;

            // notify the clock
            ////System.out.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
            //Main.writer.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
            mObj = m;
            //logging


            ////System.out.println("after send"+mObj.getId()+this.processClock.getCurrentTime());
            // get the proxy object
            Registry registry = LocateRegistry.getRegistry("127.0.0.1",
                    PortNumber.portnumber);
            RMI reciever = (RMI) registry.lookup(Integer.toString(receiverIndex));

            // send the message

            reciever.receive(mObj);

            //DelayedMessageSender sender = new DelayedMessageSender(reciever, mObj, 0);
            //new Thread(sender).start();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void processChangeRoot() {
        change_root();
    }

    public void report() {
        if (componentId == 1)
            log("try to report");
        synchronized (this) {
            //System.out.println("find count"+find_count);
            if (find_count == 0 && this.test_edge == Accept.Initial) {
                SN = State.Found;

                this.send(new Report(this.componentId, this.best_weight), this.in_branch);

                log("report to" + in_branch);
            } else {
                log("cannot report, find_count" + find_count + "test edge" + test_edge);
            }
        }
    }

    public void processReport(Report msg) {
        if (componentId == 1)
            log("process msg report from" + msg.getSenderId() + "seq" + msg.seq_id);
        synchronized (this) {
            if (msg.getSenderId() != in_branch) {
                find_count--;
                if (msg.getBest_weight() < best_weight) {
                    best_weight = msg.getBest_weight();
                    best_edge = msg.getSenderId();
                }
                report();
            } else {
                if (SN.equals(State.Find)) {
                    queue.offer(msg);

                } else {
                    if (msg.getBest_weight() > best_weight) {
                        change_root();
                    } else {
                        if (msg.getBest_weight() == best_weight && msg.getBest_weight() == Float.MAX_VALUE) {
                            log("Halt");
                            log("process: in MST:" + this.inMST);
                            log("not in MST:" + this.not_inMST);
                            log(this.FN + " level" + this.LN);
                            checkTerminate();
                            SN = State.Found;
                        }
                    }
                }
            }
        }
    }

    public void wakeup() {
        log("wakeup");
        synchronized (this) {
            int j = adjacentMinimalEdge();
            //System.out.println(this.componentId+" unknown list"+unknown_inMST);
            this.delUnknownInMst(j);
            //this.unknown_inMST.remove(j);
            this.delNotInMst(j);
            //this.not_inMST.remove(j);
            //inMST.add(j);//single
            addInMst(j);
            LN = 0;
            SN = State.Found;
            find_count = 0;
            this.send(new Connect(this.componentId, 0), j);
        }
    }

    public void change_root() {
        synchronized (this) {
            if (inMST.contains(this.best_edge)) {
                this.send(new ChangeRoot(this.componentId), this.best_edge);
            } else {
                this.send(new Connect(this.componentId, LN), this.best_edge);
                //System.out.println(this.componentId+" unknown list "+unknown_inMST);
                this.delUnknownInMst(best_edge);
                //this.unknown_inMST.remove(this.best_edge);
                this.delNotInMst(best_edge);
                //this.not_inMST.remove(this.best_edge);
                //inMST.add(this.best_edge);//single
                addInMst(best_edge);
            }
        }
    }

    public void processInitial(Initiate msg) {
        if (componentId == 1)
            log("process msg initiate from" + msg.getSenderId() + "seq" + msg.seq_id);
        synchronized (this) {
            LN = msg.getL();
            FN = msg.getF();
            SN = msg.getS();
            in_branch = msg.getSenderId();
            best_edge = Accept.Initial;
            best_weight = Float.MAX_VALUE;
            for (int i = 0; i < adjacent.length; i++) {
                if (adjacent[i] != Float.MAX_VALUE && i != msg.getSenderId() && inMST.contains(i)) {
                    send(new Initiate(this.componentId, msg.getL(), FN = msg.getF(), SN = msg.getS()), i);
                    if (msg.getS().equals(State.Find)) {
                        find_count++;
                    }
                }
            }
            if (msg.getS().equals(State.Find))
                test();

        }
    }

    public void test() {

        log(" try to test, unknown" + this.unknown_inMST);
        synchronized (this) {
            if (this.unknown_inMST.size() != 0) {
                test_edge = this.unknowMinimumEdge();
                send(new Test(this.componentId, LN, FN), test_edge);
            } else {
                test_edge = Accept.Initial;
                report();
            }
        }
    }

    public void processConnect(Connect msg) {

        log("process msg connect " + msg.getSenderId() + "seq" + msg.seq_id);
        synchronized (this) {
            if (SN.equals(State.Sleep)) {
                wakeup();
            }
            if (msg.getL() < LN) {
                //System.out.println(this.componentId+"unknown list"+unknown_inMST);
                //this.unknown_inMST.remove(msg.getSenderId());
                this.delUnknownInMst(msg.getSenderId());
                //this.not_inMST.remove(msg.getSenderId());
                this.delNotInMst(msg.getSenderId());
                //inMST.add(msg.getSenderId());//single
                addInMst(msg.getSenderId());
                send(new Initiate(this.componentId, LN, FN, SN), msg.getSenderId());
                if (SN.equals(State.Find)) {
                    find_count++;
                }
            } else {
                if (this.unknown_inMST.contains(msg.getSenderId())) {
                    this.queue.offer(msg);
                    log("cannot connect" + unknown_inMST);
                } else {
                    send(new Initiate(this.componentId, LN + 1, adjacent[msg.getSenderId()], State.Find), msg.getSenderId());
                }
            }
        }
    }

    public void processTest(Test msg) {

        log("process msg test from" + msg.getSenderId() + "seq" + msg.seq_id);
        synchronized (this) {
            if (SN.equals(State.Sleep)) {
                wakeup();
            }
            if (msg.getL() > LN) {
                queue.offer(msg);
            } else {
                if (msg.getF() != FN) {
                    send(new Accept(this.componentId), msg.getSenderId());
                } else {
					/*if (SE(j) = ?_in_MST) then SE(j) := not_in_MST 
	               if (test-edge �� j) then send(reject) on edge j  
	               else test()*/

                    if (this.unknown_inMST.contains(msg.getSenderId())) {
                        //System.out.println(this.componentId+" unknown list"+unknown_inMST);
                        //this.unknown_inMST.remove(msg.getSenderId());
                        this.delUnknownInMst(msg.getSenderId());
                        //this.inMST.remove(msg.getSenderId());
                        this.delInMst(msg.getSenderId());
                        //this.not_inMST.add(msg.getSenderId());//single
                        addNotInMst(msg.getSenderId());
                    }
                    if (test_edge != msg.getSenderId()) {
                        send(new Reject(this.componentId), msg.getSenderId());
                    } else {
                        test();
                    }
                }
            }
        }
    }

    public void processAccept(Accept msg) {

        log("process msg accept from" + msg.getSenderId() + "seq" + msg.seq_id);
        synchronized (this) {
            test_edge = Accept.Initial;
            if (adjacent[msg.getSenderId()] < best_weight) {
                best_edge = msg.getSenderId();
                best_weight = adjacent[msg.getSenderId()];
            }
            report();
        }
    }

    public void processReject(Reject msg) {
        log("process msg reject from" + msg.getSenderId());

        synchronized (this) {
            if (this.unknown_inMST.contains(msg.getSenderId())) {    //System.out.println("still unknown");
                //System.out.println(this.componentId+" unknown list "+unknown_inMST);
                //this.unknown_inMST.remove(msg.getSenderId());
                this.delUnknownInMst(msg.getSenderId());
                //this.inMST.remove(msg.getSenderId());
                this.delInMst(msg.getSenderId());
                //this.not_inMST.add(msg.getSenderId());//single
                addNotInMst(msg.getSenderId());
            }
            test();
        }
    }

    public void run() {
        boolean flag = true;

        if (componentId == totalNumber - 1 && SN.equals(State.Sleep))
            wakeup();


        while (true) {

            Message m1 = null;


            synchronized (this) {
                m1 = messageLink.poll();
            }


            if (m1 != null) {

                processMessage(m1);

            }

            Message m2 = null;

            synchronized (this) {

                m2 = queue.poll();

            }

            if (m2 != null) {

                processMessage(m2);

            }
        }
    }

    public void checkTerminate() {
        if (printed) {
            return;
        }
        synchronized (this) {

            if (unknown_inMST.size() == 0 && !printed) {
                log(inMST + " are in MST");
                log(not_inMST + " are not in MST");
                SN = State.Found;
                printed = true;
                for (int i = 0; i < adjacent.length; i++) {
                    if (adjacent[i] != Float.MAX_VALUE) {
                        send(new CheckTerminate(this.componentId), i);
                    }
                }
            }

        }
    }

    public void receive(Message message) throws RemoteException {
        messageLink.offer(message);

    }

    private long generateDelay() {
        return Math.round(Math.random() * this.maxDelay);
    }

    public int adjacentMinimalEdge() {
        synchronized (this) {
            int index = 0;
            float min = Float.MAX_VALUE;
            for (int j = 0; j < this.adjacent.length; j++) {
                if (this.adjacent[j] > 0 && this.adjacent[j] < min) {
                    index = j;
                    min = this.adjacent[j];
                }
            }
            return index;
        }
    }

    public void addInMst(int item) {
        if (!inMST.contains(item)) {
            inMST.add(item);
        }
    }

    public void addNotInMst(int item) {
        if (!not_inMST.contains(item)) {
            not_inMST.add(item);
        }
    }

    public void delInMst(int item) {
        for (int i = 0; i < this.inMST.size(); i++) {
            if (inMST.get(i) == item) {
                inMST.remove(i);
                return;
            }
        }
    }

    public void delNotInMst(int item) {
        for (int i = 0; i < this.not_inMST.size(); i++) {
            if (not_inMST.get(i) == item) {
                not_inMST.remove(i);
                return;
            }
        }
    }

    public void delUnknownInMst(int item) {
        for (int i = 0; i < this.unknown_inMST.size(); i++) {
            if (unknown_inMST.get(i) == item) {
                unknown_inMST.remove(i);
                return;
            }
        }
    }

    public int unknowMinimumEdge() {
        synchronized (this) {
            int index = this.unknown_inMST.get(0);
            float min = Float.MAX_VALUE;
            for (int i : this.unknown_inMST) {
                if (this.adjacent[i] < min) {
                    index = i;
                    min = adjacent[i];
                }
            }
            return index;
        }
    }

    public void log(String msg) {
        System.out.println("[\t" + this.componentId + "] " + msg);
    }
}
