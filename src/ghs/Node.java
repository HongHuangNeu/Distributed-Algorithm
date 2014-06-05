package ghs;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ghs.clock.VectorClock;

import ghs.message.*;


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
    private List<Connect> waitingToJoin = new LinkedList<>();

    //weight of the cheapest edge tried
    private double cheapestTried;

    //sent report to other core member?
    private boolean sentToOtherCore = false;

    //best edge I found
    private Edge bestEdge;

    //reports back in
    private List<Report> inReports = new LinkedList<>();

    public Node(int processId, int processes, VectorClock clock, float[] adjacent) throws RemoteException {
        super(processId, processes, clock);

        this.adjacent = new HashMap<>(adjacent.length);

        for (int i = 0; i < adjacent.length; i++) {
            if (adjacent[i] != Double.MAX_VALUE) {
                this.adjacent.put(i, new Edge(this.getProcessId(), i, adjacent[i]));
            }
        }
    }

    private synchronized void wakeup() {
        //find minimal cost edge
        double minCost = Double.MAX_VALUE;
        int minI = 0;
        for (int i : this.adjacent.keySet()) {
            double cost = this.adjacent.get(i).getC();
            if (cost < minCost) {
                minI = i;
                minCost = this.adjacent.get(i).getC();
            }
        }
        //mark minimal cost edge as branch
        this.adjacent.get(minI).setType(EdgeType.BRANCH);

        this.send(new Connect(this.getProcessId(), this.level), minI);
        this.state = State.FOUND;
    }

    private synchronized void processConnect(Connect m) {
        //connect both ways -> this becomes core lvl l+1

        //connect from lowel level fragment -> send initiate with my info
        //if I haven't reported, I/my fragment need to be included in the search
    }

    private synchronized void initiate() {
        //cleanup
        this.state = State.FIND;
        this.cheapestTried = Double.MAX_VALUE;
        this.sentToOtherCore = false;
        this.inReports.clear();

        // nodes in the core
        if (this.inCore()) {
            //send initate message downstream
            this.downStreamNodes().forEach(v -> {
                Node.this.send(new Initiate(this.getProcessId(), this.level, this.core, State.FIND), v);
            });

            //send to l-1 fragments waiting to join too //when put on hold???
            Stream<Connect> lMinus1 = this.waitingToJoin.stream().filter(req -> {
                return req.getLevel() == Node.this.level;
            });
            lMinus1.forEach(req -> {
                Node.this.send(new Initiate(this.getProcessId(), this.level, this.core, State.FIND), req.getFrom());
                Node.this.waitingToJoin.remove(req);
            });
        }
    }

    private synchronized void processInitiate(Initiate m) {
        //state = FIND
        this.state = State.FIND;

        //send to l-1 fragments waiting to join too
        Stream<Connect> lMinus1 = this.waitingToJoin.stream().filter(req -> {
            return req.getLevel() == Node.this.level;
        });
        lMinus1.forEach(req -> {
            Node.this.send(new Initiate(this.getProcessId(), this.level, this.core, State.FIND), req.getFrom());
            Node.this.waitingToJoin.remove(req);
        });

        //send test to minOutEdge
        Optional<Edge> minEdge = this.minOutEdge();

        if (minEdge.isPresent()) {
            this.test(minEdge.get().getV());
            this.cheapestTried = minEdge.get().getC();
        }
    }

    private synchronized void test(int v) {
        this.send(new Test(this.getProcessId(), this.core, this.level), v);
    }

    private synchronized void processTest(Test m) {
        //reply with reject if same identity (unless just sent a test to sender)
        if (this.core == m.getIdentity()) {
            this.send(new Reject(this.getProcessId()), m.getFrom());
        }
        //level greater or equal to test msg -> accept
        else if (this.level >= m.getLevel()) {
            this.send(new Accept(this.getProcessId()), m.getFrom());
        }
        //level less than test msg -> delay response until level is sufficient
        else {
            //remove old requests from m.from (could use equals as a hack...
            this.waitingToJoin.removeAll(this.waitingToJoin.stream().filter(e -> {
                return e.getFrom() == m.getFrom();
            }).collect(Collectors.toList()));
            //add a new connect (correct?)
            this.waitingToJoin.add(new Connect(m.getFrom(), m.getLevel()));
        }
    }

    private synchronized void processReject(Reject m) {
        //test next best node
        Optional<Edge> minEdge = this.minOutEdge(this.cheapestTried);
        this.cheapestTried = minEdge.get().getC();
        this.adjacent.get(m.getFrom()).setType(EdgeType.REJECTED);

        if (minEdge.isPresent()) {
            this.test(minEdge.get().getV());
        }

        //when exhausted, report (correct?)
        if (this.isLeave() && this.outEdges().isEmpty()) {
            //report empty
            this.report(null);
        }
    }

    private synchronized void processAccept(Accept m) {

        this.bestEdge = this.adjacent.get(m.getFrom());

        //when exhausted, report (correct?)
        if (this.isLeave() && this.outEdges().isEmpty()) {
            //report m
            this.report(this.adjacent.get(m.getFrom()));
        }
    }

    private synchronized void report(Edge bestEdge) {
        //state = FOUND
        this.state = State.FOUND;

        //if leave, report my minOutEdge
        if (this.isLeave()) {
            this.send(new Report(this.getProcessId(), bestEdge), this.parent);
            if (this.isLeave()) {
                this.sentToOtherCore = true;
            }
        }
        //report to parent
        else if (this.isInterior()) {
            this.send(new Report(this.getProcessId(), bestEdge), this.parent);
        }
        //if core send to other core (NO INF LOOP!)
        else if (!this.sentToOtherCore) {
            this.sentToOtherCore = true;
            //TODO
        }
    }

    private synchronized void processReport(Report m) {
        this.inReports.add(m);

        //if interior combine mine + children
        if (this.isInterior() && allReportsIn()) {
            this.report(this.computeBestEdge());
        }

        //if core send to other core (NO INF LOOP!)
        else if (this.inCore()) {
            if (this.sentToOtherCore) {
                this.bestEdge = this.computeBestEdge();
                this.outEdges().forEach(e -> {
                    this.send(new ChangeCore(this.getProcessId(), this.core, this.level), e.getV());
                });
            }

            else if (allReportsIn()) {
                this.report(this.computeBestEdge());
                this.bestEdge = this.computeBestEdge();
            }
        }
    }

    private synchronized void processChangeCore() {
    }

    private boolean allReportsIn() {
        return this.inReports.size() == this.outEdges().size();
    }

    public synchronized void processChangeCore(Report m) {
        //if best-edge is mine send it a connect

        //pass to outbound
    }

    @Override
    public void processMessage(Payload p) {
        if(p instanceof Connect)
        {
            this.processConnect((Connect) p);
        }
        if(p instanceof ChangeCore)
        {
            this.processChangeCore();
        }
        if(p instanceof Accept)
        {
            this.processAccept((Accept) p);
        }
        if(p instanceof Initiate)
        {
            this.processInitiate((Initiate) p);
        }
        if(p instanceof Reject)
        {
            this.processReject((Reject) p);
        }
        if(p instanceof Report)
        {
            this.processReport((Report) p);
        }
        if(p instanceof Test)
        {
            this.processTest((Test) p);
        }
    }

    private synchronized Edge computeBestEdge() {
        return this.inReports.stream().map(Report::getBestEdge).reduce(this.bestEdge, (edge1, edge2) -> {
            return edge1.getC() < edge2.getC() ? edge1 : edge2;
        });
    }

    private Optional<Edge> minOutEdge() {
        return this.minOutEdge(Double.MAX_VALUE);
    }

    private java.util.Optional<Edge> minOutEdge(double higherThan) {
        //cheapest edge of type basic
        return this.outEdges().stream().filter(e -> {
            return e.getC() > higherThan;
        }).min(Comparator.<Edge>naturalOrder());
    }

    private synchronized boolean inCore() {
        //is the identity of my fragement the same as the cost of my edges?
        return this.adjacent.values().stream().anyMatch(e -> {
            return e.getC() == Node.this.core;
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

    private synchronized Iterable<Integer> downStreamNodes() {
        //get the other end of all branch edges that are not the parent
        return this.adjacent.values().stream().filter(e -> {
            return (e.getType() == EdgeType.BRANCH) && e.getV() != this.parent;
        }).map(Edge::getV).collect(Collectors.<Integer>toList());
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
