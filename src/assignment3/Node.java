package assignment3;

import java.rmi.RemoteException;

import assignment3.Process;
import assignment3.clock.VectorClock;

import assignment3.message.*;


public class Node extends Process {

    private static final long serialVersionUID = 7247714666080613254L;
    private int LN;
    private float FN;
    private State SN=State.Sleep;
    private int in_branch;
    private int test_edge = Accept.Initial;
    private int best_edge;
    private float best_weight;
    private int find_count;
    private float[] adjacent;
	
	public Node(int processId, int processes, VectorClock clock, float[] adjacent) throws RemoteException {
		super(processId, processes, clock);

        this.adjacent = adjacent;
	}

    @Override
    public void processMessage(Message m) {

        if(m instanceof Connect)
        {
            this.processConnect((Connect)m);
        }
        if(m instanceof ChangeRoot)
        {
            this.processChangeRoot();
        }
        if(m instanceof Accept)
        {
            this.processAccept((Accept)m);
        }
        if(m instanceof Initiate)
        {
            this.processInitial((Initiate)m);
        }
        if(m instanceof Reject)
        {
            this.processReject((Reject)m);
        }
        if(m instanceof Report)
        {
            this.processReport((Report)m);
        }
        if(m instanceof Test)
        {
            this.processTest((Test)m);
        }
        if(m instanceof CheckTerminate)
        {
            checkTerminate();
        }
    }
}
