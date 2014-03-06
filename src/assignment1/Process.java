package assignment1;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import assignment1.clock.Clock;

//@SuppressWarnings("serial")
public class Process<T> extends UnicastRemoteObject implements RMI<T>,
		Runnable, Serializable {
	private static final long serialVersionUID = 7247714666080613254L;
	protected String processName;
	protected int processIndex;
	protected Clock<T> processClock;
	public static int round = 0;
	
	public Process(String processName, int processIndex, Clock<T> clock)
			throws RemoteException {

		super();
		this.processName = processName;
		this.processIndex = processIndex;
		this.processClock=clock;
		try {
			Registry registry = LocateRegistry.getRegistry(4303);
			registry.rebind(processName, this);
			System.out.println("bind successful");
			System.setProperty("java.security.policy", Process.class
			.getResource("my.policy").toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Bind fail");
		}

	}

	public void send(String m, String recieverName)
	{
		//logging
		System.out.println("sent to process " + recieverName + "from process "
					+ processName + " message:" + m);
		
		try
		{
			// wrap message contents m into message object
			Message<T> mObj;
			synchronized(Main.id)
			{
				mObj = new Message<T>(this.processName, this.processIndex, m, Main.id++, this.processClock.getCurrentTime(),recieverName);
			}
			
			// get the proxy object
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",
					4303);
			RMI<T> reciever = (RMI<T>) registry.lookup(recieverName);
			
			// notify the clock
			this.processClock.updateSent(this.processIndex);
			
			// send the message
			DelayedMessageSender<T> sender = new DelayedMessageSender<T>(this, reciever, mObj, 1000);
			new Thread(sender).start();

		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
	}
	
	public void run() {
		for (int i = 0; i < 100; i++) {
			int j = i % 3;
			if (Integer.valueOf(processName) != j) {
				send("m", String.valueOf(j));
			}
		}
		System.out.println("finish sending");
	}

	public void receive(Message<T> message) throws RemoteException {
		// notify the clock
		this.processClock.updateRecieved(message.getSentAt());
		System.out.println(processName + " receive from process "
				+ message.getSenderName() + " message:" + message.getMessage()
				+ " message number " + message.getId());
		addToMessage(message);
	}
	public void addToMessage(Message<T> message)
	{
		
	}
	public void setClock(Clock<T> processClock)
	{
		this.processClock=processClock;
	}
	public Clock<T> getClock()
	{
		return this.processClock;
	}
	public int getProcessIndex()
	{
		return this.processIndex;
	}
}
