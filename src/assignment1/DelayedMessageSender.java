package assignment1;

import java.rmi.RemoteException;

public class DelayedMessageSender<T> implements Runnable
{
	private RMI<T> from;
	private RMI<T> to;
	private Message<T> m;
	
	private long maxDelay = 0;
	
	public DelayedMessageSender(RMI<T> from, RMI<T> to, Message<T> m, long maxDelay)
	{
		this.from = from;
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
