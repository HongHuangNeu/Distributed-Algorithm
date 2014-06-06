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
    private int level = 0;

    //the 'root' of the fragment this node belongs to
    private double core;

    //node on the route to the core
    private int parent;

    //the waiting list
    //FIXME should this be queue?
    private Queue<Payload> waitingToJoin = new LinkedList<>();

    //weight of the cheapest edge tried
    private double heaviestWeightTried;

    //route to the best edge found
    private int bestEdge;

    //best weight found
    private double bestWeight;

    //reports back in
    private List<Report> inReports = new LinkedList<>();

    //expected reports
    private int expectedReports = 0;

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
            //FIXME cleanup everytime
            Edge best = m.get();
            best.setType(EdgeType.BRANCH);
            this.level = 0;
            this.inReports.clear();
            this.expectedReports = 0;
            this.send(new Connect(this.getProcessId(), this.level), best.getV());
        } else {
            //TODO terminate
        }
    }

    private synchronized void processConnect(Connect m) {
        if (this.state == State.SLEEPING) {
            this.wakeup();
        }

        Optional<Edge> se = this.minOutEdge();

        if (se.isPresent()) {
            Edge e = se.get();
            if (this.level > m.getLevel()) {
                e.setType(EdgeType.BRANCH);
                this.send(new Initiate(this.getProcessId(), this.level, this.core, this.state), e.getV());

                if (this.state == State.FIND) {
                    this.expectedReports++;
                }
            } else if (e.getType() == EdgeType.BRANCH) {
                this.waitingToJoin.offer(m);
            } else {
                this.send(new Initiate(this.getProcessId(), this.level + 1, e.getW(), State.FIND), e.getV());
            }
        } else {
            //TODO terminate?
        }
    }

    private synchronized void processInitiate(Initiate m) {
        this.level = m.getLevel();
        this.core = m.getCore();
        this.state = m.getState();

        this.parent = m.getFrom();
        this.bestEdge = -1;

        this.downStreamEdges().stream().forEach(e -> {
            this.send(new Initiate(this.getProcessId(), this.level, this.core, this.state), e.getV());

            if (this.state == State.FIND) {
                this.expectedReports++;
            }
        });

        if (this.state == State.FIND) {
            this.test();
        }
    }

    private synchronized void test() {
        Optional<Edge> testEdgeOptional = this.minOutEdge();

        if (testEdgeOptional.isPresent()) {
            Edge testEdge = testEdgeOptional.get();
            this.send(new Test(this.getProcessId(), this.level, this.core), testEdge.getV());
            this.heaviestWeightTried = testEdge.getW();
        } else {
            this.report();
        }
    }

    private synchronized void processTest(Test m) {
        if (this.state == State.SLEEPING) {
            this.wakeup();
        }

        if (this.level < m.getLevel()) {
            this.waitingToJoin.offer(m);
        } else {
            Edge j = this.adjacent.get(m.getFrom());

            if (j.getType() == EdgeType.BRANCH) {
                j.setType(EdgeType.REJECTED);
            }

            if (this.heaviestWeightTried == this.adjacent.get(m.getFrom()).getW()) {
                this.send(new Reject(this.getProcessId()), j.getV());
            } else {
                this.test();
            }
        }
    }

    private synchronized void processAccept(Accept m) {
        this.heaviestWeightTried = Double.MAX_VALUE;
        Edge j = this.adjacent.get(m.getFrom());

        this.updateBestEdge(j.getW(), j.getV());

        report();
    }

    private synchronized void processReject(Reject m) {
        Edge j = this.adjacent.get(m.getFrom());

        if (j.getType() == EdgeType.BASIC) {
            j.setType(EdgeType.REJECTED);
        }

        test();
    }

    private synchronized void report() {
        if (this.allReportsIn() && this.bestEdge != -1) {
            this.state = State.FOUND;
            this.send(new Report(this.getProcessId(), this.bestWeight), this.parent);
        }
    }

    private synchronized void processReport(Report m) {
        Edge j = this.adjacent.get(m.getFrom());

        if (j.getV() != this.parent) {
            this.updateBestEdge(m.getBestWeight(), j.getV());
            this.report();
        } else {
            if (this.state == State.FIND) {
                this.waitingToJoin.offer(m);
            } else {
                if (m.getBestWeight() > this.bestWeight) {
                    this.changeRoot();
                } else if (m.getBestWeight() == Double.MAX_VALUE && this.bestWeight == Double.MAX_VALUE) {
                    //todo halt
                    log("halt");
                }
            }
        }
    }

    private synchronized void changeRoot() {
        if (this.adjacent.get(this.bestEdge).getType() == EdgeType.BRANCH) {
            this.send(new ChangeCore(this.getProcessId()), this.bestEdge);
        } else {
            this.send(new Connect(this.getProcessId(), this.level), this.bestEdge);
            this.adjacent.get(this.bestEdge).setType(EdgeType.BRANCH);
        }
    }

    private synchronized void processChangeCore(ChangeCore m) {
        this.changeRoot();
    }

    public synchronized void processChangeCore(Report m) {
    }

    @Override
    public void processMessage(Payload p) {
        if (p instanceof Connect) {
            this.processConnect((Connect) p);
        }
        if (p instanceof ChangeCore) {
            this.processChangeCore((ChangeCore) p);
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

        //this.processQueue();
    }

    private synchronized void processQueue() {
        this.processMessage(this.waitingToJoin.poll());
    }

    private synchronized void updateBestEdge(double w, int from) {
        if (w < this.bestWeight) {
            this.bestEdge = from;
            this.bestWeight = w;
        }
    }

    private boolean allReportsIn() {
        return this.inReports.size() == this.expectedReports;
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

    private synchronized boolean inCore() {
        //is the identity of my fragement the same as the cost of my edges?
        return this.adjacent.values().stream().anyMatch(e -> {
            return e.getW() == Node.this.core;
        });
    }

    private synchronized boolean isLeave() {
        //adjacent to only 1 branch?
        return this.adjacent.values().stream().filter(e -> {
            return e.getType() == EdgeType.BRANCH;
        }).count() == 1;
    }

    private synchronized boolean isInterior() {
        return !(this.inCore() && this.isLeave());
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

    private synchronized List<Edge> branches() {
        return this.adjacent.values().stream().filter(e -> {
            return e.getType() == EdgeType.BRANCH;
        }).collect(Collectors.<Edge>toList());
    }
}
