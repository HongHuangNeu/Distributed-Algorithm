package assignment1;

import java.rmi.Naming;

public class Delay implements RemoteProcess_RMI<T>,Runnable {

	/**
	 * @param args
	 */
public T getTime()
{}
	
	public void send(Object messange)
	{
		RemoteProcess_RMI<T> target=(RemoteProcess_RMI)Naming.lookup("localhost:1099/"+message.getObject().getName());
		
	}
	public void recieve(T timeStamp, Object message)
	{
		
	}
    public void run()
    {
    	
    }
}
