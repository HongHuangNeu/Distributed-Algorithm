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
	
	
	private Map<Integer,VectorTimeStamp> timeStampBuffer;   //Local Buffer
	public ArrayList<Message> messageBuffer=new ArrayList<Message>();// Buffer to include undelivered message
	
	public Process( int processIndex, VectorClock clock)
			throws RemoteException {

		super();
		this.processIndex = processIndex;
		this.processClock=clock;
		timeStampBuffer=new HashMap();
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
		
		
		try
		{
			
			// wrap message contents m into message object
			Message mObj;
			synchronized(this)
			{
			// notify the clock
			this.processClock.updateSent();
		
			
				//System.out.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
				//Main.writer.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
				
				mObj = new Message(this.processIndex, m, Main.id++,this.processClock.getCurrentTime(),receiverIndex,copyBuffer(this.timeStampBuffer));
				//logging
				System.out.println("sent to process " + receiverIndex + "from process "
							+ processIndex + " message number:" + mObj.getId());
			
				updateBufferAfterSend(receiverIndex);
			}
			//System.out.println("after send"+mObj.getId()+this.processClock.getCurrentTime());
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
				+ " message number " + message.getId()+"vector clock is "+message.getTimeStampBuffer()+" local clock "+this.processClock.getCurrentTime());

		synchronized(this){
			if(this.canDeliver(message))
			{
				this.deliver(message);
				while(!this.processBuffer())
				{
					this.processBuffer();
				}
			}
			else
			{
				System.out.println("buffer messgae"+message.getId());
				this.messageBuffer.add(message);
			}
		}
	}
	
	private boolean processBuffer()
	{
		
		Message toDeliver = null;
		
		
			
			for(Message m : this.messageBuffer)
			{
				toDeliver = null;
				
				if(this.canDeliver(m))
				{
					//Deliverable message found!
					toDeliver = m;
					//local time stamp changes here
					this.messageBuffer.remove(m);
					
					this.deliver(toDeliver);
					return false;
				}
			}
	
			
		System.out.println("process "+this.processIndex+ "remaining sie "+this.messageBuffer.size()+"my clock is"+this.processClock.getCurrentTime());
		System.out.println("process "+this.processIndex+ "remaining "+this.messageBuffer);
		return true;
	}
	
	private boolean canDeliver(Message m)
	{
		if(!m.getTimeStampBuffer().containsKey(this.processIndex))
		{
			return true;
		}
		VectorTimeStamp expected = m.getTimeStampBuffer().get(this.processIndex);
		
		return this.processClock.getCurrentTime().biggerOrEqual(expected);
	}
	
	private void deliver(Message m)
	{
		//this.messageBuffer.remove(m);

		this.mergeLocalBuffer(m);
		this.processClock.updateRecieved(m.getSentAt());
		System.out.println("deliver "+m.getId()+"to process "+this.processIndex);
	}
	
	public void mergeLocalBuffer(Message message)
	{
	
		for(Integer key:message.getTimeStampBuffer().keySet())
		{
			VectorTimeStamp value=message.getTimeStampBuffer().get(key);
			if(this.timeStampBuffer.containsKey(key)&&key!=this.processIndex)
			{
				VectorTimeStamp myValue=this.timeStampBuffer.get(key);
				this.timeStampBuffer.put(key, value.max(myValue));
			}
			else
			{
				if(key!=this.processIndex)
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
	
	Map<Integer,VectorTimeStamp> copyBuffer(Map<Integer,VectorTimeStamp> localBuffer)
	{
		Map<Integer,VectorTimeStamp> result=new HashMap(); 
		for(Integer key:localBuffer.keySet())
		{
			result.put(key, new VectorTimeStamp(localBuffer.get(key).getTime()));
		}
		return result;
	}
	
}
