package ghs;

import ghs.clock.VectorClock;
import ghs.clock.VectorTimeStamp;
import ghs.message.Message;
import ghs.message.Payload;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//@SuppressWarnings("serial")
public class Process extends UnicastRemoteObject implements RMI,
        Runnable, Serializable {
    private static final long serialVersionUID = 7247714666080613254L;

    private int processId;
    private VectorClock processClock;
    public static int round = 0;
    private int messagesSent = 0;
    private int processes = 0;

    private Map<Integer, VectorTimeStamp> timeStampBuffer;   //Local Buffer
    public ArrayList<Message> messageBuffer = new ArrayList<Message>();// Buffer to include undelivered message

    public Process(int processIndex, int processes, VectorClock clock)
            throws RemoteException {

        super();
        this.processId = processIndex;
        this.processClock = clock;
        timeStampBuffer = new HashMap<Integer, VectorTimeStamp>();
        try {

            Registry registry = LocateRegistry.getRegistry(4303);
            registry.rebind(Integer.toString(processIndex), this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void send(Payload p, int to) {
        try {
            synchronized (this) {
                // notify the clock
                this.processClock.updateSent();
                // wrap payload p into message object
                Message m = new Message(this.processId, to, this.getProcessClock().getCurrentTime(), this.timeStampBuffer, p);
                updateBufferAfterSend(m.getReceiverId());

                // get the process proxy
                Registry registry = LocateRegistry.getRegistry("127.0.0.1",
                        4303);
                RMI reciever = (RMI) registry.lookup(Integer.toString(m.getReceiverId()));

                // send the message
                this.messagesSent++;
                DelayedMessageSender sender = new DelayedMessageSender(this.processId, this.processClock.getCurrentTime(), reciever, m, 1000);
                new Thread(sender).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        log("Up and running");
        while (true);
    }

    synchronized public void receive(Message message) throws RemoteException {
        if (this.canDeliver(message)) {
            this.deliver(message);
            while (!this.processBuffer()) {
                this.processBuffer();
            }
        } else {
            this.messageBuffer.add(message);
        }
    }

    private boolean processBuffer() {

        Message toDeliver = null;

        for (Message m : this.messageBuffer) {
            toDeliver = null;
            if (this.canDeliver(m)) {
                //Deliverable message found!
                toDeliver = m;
                //local time stamp changes here
                this.messageBuffer.remove(m);

                this.deliver(toDeliver);
                return false;
            }
        }

        return true;
    }

    private boolean canDeliver(Message m) {
        if (!m.getTimeStampBuffer().containsKey(this.processId)) {
            return true;
        }

        VectorTimeStamp expected = m.getTimeStampBuffer().get(this.processId);
        return this.processClock.getCurrentTime().biggerOrEqual(expected);
    }

    private void deliver(Message m) {
        //this.messageBuffer.remove(m);

        this.mergeLocalBuffer(m);
        this.processClock.updateRecieved(m.getSentAt());
        this.processMessage(m.getPayload());
    }

    protected void processMessage(Payload p) {
    }

    public void mergeLocalBuffer(Message message) {

        for (Integer key : message.getTimeStampBuffer().keySet()) {
            VectorTimeStamp value = message.getTimeStampBuffer().get(key);
            if (this.timeStampBuffer.containsKey(key) && key != this.processId) {
                VectorTimeStamp myValue = this.timeStampBuffer.get(key);
                this.timeStampBuffer.put(key, value.max(myValue));
            } else {
                if (key != this.processId)
                    this.timeStampBuffer.put(key, value);
            }


        }
    }

    public int getProcessId() {
        return this.processId;
    }

    public VectorClock getProcessClock() {
        return this.processClock;
    }

    public void updateBufferAfterSend(int receiverIndex) {
        synchronized (this.timeStampBuffer) {
            this.timeStampBuffer.put(receiverIndex, this.processClock.getCurrentTime());
        }
    }

    private Map<Integer, VectorTimeStamp> copyBuffer(Map<Integer, VectorTimeStamp> localBuffer) {
        Map<Integer, VectorTimeStamp> result = new HashMap<Integer, VectorTimeStamp>();
        for (Integer key : localBuffer.keySet()) {
            result.put(key, new VectorTimeStamp(localBuffer.get(key).getTime()));
        }
        return result;
    }

    protected void log(String msg) {
        System.out.println("[" + this.getProcessId() + "\t] " + msg);
    }
}
