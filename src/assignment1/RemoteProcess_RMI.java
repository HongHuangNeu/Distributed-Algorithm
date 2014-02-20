package assignment1;

import java.rmi.Remote;

public interface RemoteProcess_RMI<T extends TimeStamp<?>> extends Remote{
	public T getTime();
	
	public void send(Message<VectorTimeStamp> messange);
	public void recieve(Message<VectorTimeStamp> message);
}
