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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import assignment1.clock.VectorClock;
import assignment1.clock.VectorTimeStamp;

//@SuppressWarnings("serial")
public class Process<T> extends UnicastRemoteObject implements RMI<T>,
		Runnable, Serializable {
	private static final long serialVersionUID = 7247714666080613254L;
	
	protected int processIndex;
	protected VectorClock processClock;
	public static int round = 0;
	
	
	private Map<Integer,VectorTimeStamp> timeStampBuffer=new HashMap();   //Local Buffer
	public ArrayList<Message> messageBuffer=new ArrayList<Message>();// Buffer to include undelivered message
	
	public Process( int processIndex, VectorClock clock)
			throws RemoteException {

		super();
		this.processIndex = processIndex;
		this.processClock=clock;
		try {
			Registry registry = LocateRegistry.getRegistry(4303);
			registry.rebind(Integer.toString(processIndex), this);
			System.out.println("bind successful");
			System.setProperty("java.security.policy", Process.class
			.getResource("my.policy").toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Bind fail");
		}

	}

	public void send(String m, int receiverIndex)
	{
		//logging
		System.out.println("sent to process " + receiverIndex + "from process "
					+ processIndex + " message:" + m);
		
		try
		{
			// wrap message contents m into message object
			Message mObj;
			
			// notify the clock
			this.processClock.updateSent(this.processIndex);
			synchronized(Main.id)
			{
				mObj = new Message(this.processIndex, m, Main.id++, this.processClock.getCurrentTime(),receiverIndex,this.timeStampBuffer);
			}
			updateBufferAfterSend(receiverIndex);
			// get the proxy object
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",
					4303);
			RMI<T> reciever = (RMI<T>) registry.lookup(Integer.toString(receiverIndex));
			
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
			if (processIndex != j) {
				send("m", j);
			}
		}
		System.out.println("finish sending");
	}
	
	public void receive(Message message) throws RemoteException {
		// notify the clock
		
		System.out.println(processIndex + " receive from process "
				+ message.getSenderIndex() + " message:" + message.getMessage()
				+ " message number " + message.getId());

		synchronized(this){
			if(this.canDeliver(message))
			{
				this.deliver(message);
				this.processBuffer();
			}
			else
			{
				this.messageBuffer.add(message);
			}
		}
	}
	
	private void processBuffer()
	{
		boolean updated = true;
		Message toDeliver = null;
		
		while(updated)
		{
			for(Message m : this.messageBuffer)
			{
				toDeliver = null;
				
				if(this.canDeliver(m))
				{
					//Deliverable message found!
					toDeliver = m;
					break;

				}
			}
			
			if(toDeliver != null)
			{
				//local time stamp changes here
				this.deliver(toDeliver);
				updated = true;
			}
			else
			{
				updated = false;
			}
		}
	}
	
	private boolean canDeliver(Message m)
	{
		VectorTimeStamp expected = m.getTimeStampBuffer().get(this.processIndex);
		
		return this.processClock.getCurrentTime().biggerOrEqual(expected);
	}
	
	private void deliver(Message m)
	{
		this.messageBuffer.remove(m);
		this.processClock.updateRecieved(m.getSentAt());
		this.mergeLocalBuffer(m);
		System.out.println("deliver "+m.getId());
	}
	
	public void mergeLocalBuffer(Message message)
	{
	
		for(Integer key:message.getTimeStampBuffer().keySet())
		{
			VectorTimeStamp value=message.getTimeStampBuffer().get(key);
			if(this.timeStampBuffer.containsKey(key))
			{
				VectorTimeStamp myValue=this.timeStampBuffer.get(key);
				this.timeStampBuffer.put(key, value.max(myValue));
			}
			else
			{
				this.timeStampBuffer.put(key, value);
			}
			
			
		}
	}
	
	public int getProcessIndex()
	{
		return this.processIndex;
	}
	
	public void updateBufferAfterSend(int receiverIndex)
	{
		synchronized(this.timeStampBuffer){
		this.timeStampBuffer.put(receiverIndex, this.processClock.getCurrentTime());
		}
	}
}
