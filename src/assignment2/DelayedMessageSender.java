package assignment2;

import java.rmi.RemoteException;

public class DelayedMessageSender<T> implements Runnable
{

	private RMI to;
	private Message m;
	
	private long maxDelay = 0;
	
	public DelayedMessageSender( RMI to, Message m, long maxDelay)
	{
	
		this.to = to;
		this.m = m;
		this.maxDelay = maxDelay;
	}
	
	public void run()
	{
		long delay = this.generateDelay();
		
		try
		{
			Thread.sleep(delay);
			
			synchronized(this.to)
			{
				to.receive(this.m);
			}
		}
		
		catch (InterruptedException e)
		{
			// Can't sleep!
			e.printStackTrace();
		}
		catch (RemoteException e)
		{
			// Remote exception
			e.printStackTrace();
		}
		
		
	}
	
	private long generateDelay()
	{
		return Math.round(Math.random() * this.maxDelay);
	}
}
