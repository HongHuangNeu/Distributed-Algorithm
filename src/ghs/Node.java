package ghs;

import ghs.clock.VectorClock;
import ghs.message.*;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;


public class Node extends Process {

    private static final long serialVersionUID = 7247714666080613254L;


    //distance to the other nodes. Double.MAX_VALUE means not connected
    private Map<Integer, Edge> adjacent;

    //state of this node,
    //initially SLEEPING
    //FIND when participating
    //otherwise FOUND
    private State state = State.SLEEPING;

    //indication of the size of this fragment
    private int level;

    //the 'root' of the fragment this node belongs to
    private double core;

    //node on the route to the core
    private int parent;

    //the waiting list
    //FIXME should this be queue?
    private Queue<Payload> waitingToJoin = new LinkedList<>();

    //Edge being tested
    private int testEdge;

    //route to the best edge found
    private int bestEdge;

    //best weight found
    private double bestWeight;

    //expected reports
    private int findCount;

    public Node(int processId, int processes, VectorClock clock, double[] adjacent) throws RemoteException {
        super(processId, processes, clock);

        this.adjacent = new HashMap<>(adjacent.length);

        for (int i = 0; i < adjacent.length; i++) {
            if (adjacent[i] != Double.MAX_VALUE) {
                this.adjacent.put(i, new Edge(this.getProcessId(), i, adjacent[i]));
            }
        }
    }

    public synchronized void wakeup() {
        log("wakeup");
        Optional<Edge> m = this.minOutEdge();
        if (m.isPresent()) {
            Edge best = m.get();
            best.setType(EdgeType.BRANCH);
            this.state = State.FOUND;
            this.level = 0;
            this.findCount = 0;
            this.send(new Connect(this.getProcessId(), this.level), best.getV());

        } else {
            //TODO terminate
        }
    }

    private synchronized void processConnect(Connect m) {
        log("process connect from"+m.getFrom());
        if (this.state == State.SLEEPING) {
            this.wakeup();
        }

        Edge j = this.adjacent.get(m.getFrom());

        if (this.level > m.getLevel()) {
            j.setType(EdgeType.BRANCH);
            this.send(new Initiate(this.getProcessId(), this.level, this.core, this.state), m.getFrom());

            if (this.state == State.FIND) {
                this.findCount++;
            }
        } else if (j.getType() == EdgeType.BASIC) {
            this.waitingToJoin.offer(m);
        } else {
            this.send(new Initiate(this.getProcessId(), this.level + 1, j.getW(), State.FIND), m.getFrom());
        }
    }

    private synchronized void processInitiate(Initiate m) {
        log("process initiate");
        this.level = m.getLevel();
        this.core = m.getCore();
        this.state = m.getState();

        this.parent = m.getFrom();
        this.bestEdge = -1;
        this.bestWeight = Double.MAX_VALUE;

        this.downStreamEdges().stream().forEach(e -> {
            this.send(new Initiate(this.getProcessId(), this.level, this.core, this.state), e.getV());

            if (this.state == State.FIND) {
                this.findCount++;
            }
        });

        if (this.state == State.FIND) {
            this.test();
        }
    }

    private synchronized void test() {
        log("test");
        Optional<Edge> testEdgeOptional = this.minOutEdge();

        if (testEdgeOptional.isPresent()) {
            Edge testEdge = testEdgeOptional.get();
            this.send(new Test(this.getProcessId(), this.level, this.core), testEdge.getV());
            this.testEdge = testEdge.getV();
        } else {
            this.testEdge = -1;
            this.report();
        }
    }

    private synchronized void processTest(Test m) {
        log("process test from"+m.getFrom());
        if (this.state == State.SLEEPING) {
            this.wakeup();
        }

        if (this.level < m.getLevel()) {
            this.waitingToJoin.offer(m);
        } else {
        
            Edge j = this.adjacent.get(m.getFrom());
          //You forgot to add this if!
            if(this.core!=m.getIdentity())
            {
            	this.send(new Accept(this.getProcessId()), m.getFrom());
            }else{
	            if (j.getType() == EdgeType.BASIC) {
	                j.setType(EdgeType.REJECTED);
	            }
	            
	            if (this.testEdge != m.getFrom()) {
	                this.send(new Reject(this.getProcessId()), m.getFrom());
	            } else {
	                this.test();
	            }
            }
        }
    }

    private synchronized void processAccept(Accept m) {
        log("process accept");
        this.testEdge = -1;
        Edge j = this.adjacent.get(m.getFrom());

        if (this.bestWeight > j.getW()) {
            this.bestEdge = m.getFrom();
            this.bestWeight = j.getW();
        }

        report();
    }

    private synchronized void processReject(Reject m) {
        log("process reject");
        Edge j = this.adjacent.get(m.getFrom());

        if (j.getType() == EdgeType.BASIC) {
            j.setType(EdgeType.REJECTED);
        }

        test();
    }

    private synchronized void report() {
        log("report");
        if (this.findCount == 0 && this.testEdge == -1) {
            this.state = State.FOUND;
            this.send(new Report(this.getProcessId(), this.bestWeight), this.parent);
        }
    }

    private synchronized void processReport(Report m) {
        log("process report from"+m.getFrom());
        Edge j = this.adjacent.get(m.getFrom());

        if (j.getV() != this.parent) {
            this.findCount--;
            if (this.bestWeight > m.getW()) {
                this.bestEdge = m.getFrom();
                this.bestWeight = m.getW();
            }
            this.report();
        } else {
            if (this.state == State.FIND) {
                this.waitingToJoin.offer(m);
            } else {
                if (this.bestWeight < m.getW()) {
                    this.changeRoot();
                } else if (m.getW() == Double.MAX_VALUE && this.bestWeight == Double.MAX_VALUE) {
                    //todo halt
                    log("halt level"+this.level);
                }
            }
        }
    }

    private synchronized void changeRoot() {
        log("change root");
        if (this.adjacent.get(this.bestEdge).getType() == EdgeType.BRANCH) {
            this.send(new ChangeRoot(this.getProcessId()), this.bestEdge);
        } else {
            this.send(new Connect(this.getProcessId(), this.level), this.bestEdge);
            this.adjacent.get(this.bestEdge).setType(EdgeType.BRANCH);
        }
    }

    private synchronized void processChangeRoot(ChangeRoot m) {
        log("change root");
        this.changeRoot();
    }

    @Override
    public void processMessage(Payload p) {
        if (p instanceof Connect) {
            this.processConnect((Connect) p);
        }
        if (p instanceof ChangeRoot) {
            this.processChangeRoot((ChangeRoot) p);
        }
        if (p instanceof Accept) {
            this.processAccept((Accept) p);
        }
        if (p instanceof Initiate) {
            this.processInitiate((Initiate) p);
        }
        if (p instanceof Reject) {
            this.processReject((Reject) p);
        }
        if (p instanceof Report) {
            this.processReport((Report) p);
        }
        if (p instanceof Test) {
            this.processTest((Test) p);
        }
    }

    @Override
    public void run() {
        while (true) {
            this.processQueue();
            try {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deliver(Message m) {
        // pass to logger


        super.deliver(m);
    }

    private synchronized void processQueue() {
        if (!this.waitingToJoin.isEmpty()) {
            this.processMessage(this.waitingToJoin.poll());
        }
    }

    private Optional<Edge> minOutEdge() {
        return this.minOutEdge(0);
    }

    private java.util.Optional<Edge> minOutEdge(double higherThan) {
        //cheapest edge of type basic
        return this.outEdges().stream().filter(e -> {
            return e.getW() > higherThan;
        }).min(Comparator.<Edge>naturalOrder());
    }

    private synchronized List<Edge> downStreamEdges() {
        //get the other end of all branch edges that are not the parent
        return this.adjacent.values().stream().filter(e -> {
            return (e.getType() == EdgeType.BRANCH) && e.getV() != this.parent;
        }).collect(Collectors.<Edge>toList());
    }

    private synchronized List<Edge> outEdges() {
        return this.adjacent.values().stream().filter(e -> {
            return e.getType() == EdgeType.BASIC;
        }).collect(Collectors.<Edge>toList());
    }
}
