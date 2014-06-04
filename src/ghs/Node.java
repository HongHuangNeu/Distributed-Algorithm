package ghs;

import java.rmi.RemoteException;

import ghs.clock.VectorClock;

import ghs.message.*;


public class Node extends Process {

    private static final long serialVersionUID = 7247714666080613254L;



	public Node(int processId, int processes, VectorClock clock, float[] adjacent) throws RemoteException {
		super(processId, processes, clock);
	}

    @Override
    public void processMessage(Payload p) {
    }
}
