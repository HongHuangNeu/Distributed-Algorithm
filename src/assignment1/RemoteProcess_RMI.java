package assignment1;

import java.rmi.Remote;

public interface RemoteProcess_RMI<T extends TimeStamp> extends Remote{
	public T getTime();
	
	public void send(Object messange);
	public void recieve(T timeStamp, Object message);
}
