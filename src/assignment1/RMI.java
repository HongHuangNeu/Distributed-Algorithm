package assignment1;
import java.rmi.Remote;
import java.rmi.RemoteException;

import assignment1.clock.VectorClock;

public interface RMI<T> extends Remote{
	public void receive(Message message)throws RemoteException;
}
