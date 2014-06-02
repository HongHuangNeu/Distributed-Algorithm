package assignment1;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
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
	private int messagesSent = 0;
	private int processes = 0;
	
	private Map<Integer,VectorTimeStamp> timeStampBuffer;   //Local Buffer
	public ArrayList<Message> messageBuffer=new ArrayList<Message>();// Buffer to include undelivered message
	
	public Process(int processIndex, int processes, VectorClock clock)
			throws RemoteException {

		super();
		this.processIndex = processIndex;
		this.processClock=clock;
		timeStampBuffer=new HashMap();
		try {
			
			Registry registry = LocateRegistry.getRegistry(4303);
			registry.bind(Integer.toString(processIndex), this);
			System.out.println("[" + this.processIndex + "] bind successful");
		}
		catch(RemoteException e)
		{
			System.out.println("remoteException");
			e.printStackTrace();
		}
		catch(AlreadyBoundException e)
		{
			System.out.println("already Bound");
			e.printStackTrace();
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
		
			
			//System.out.println("[" + this.processIndex + "]" + this.processClock.getCurrentTime() + " number before send"+(Main.id+1)+this.processClock.getCurrentTime());
			//Main.writer.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
			
			mObj = new Message(this.processIndex, m, Main.id++,this.processClock.getCurrentTime(),receiverIndex,copyBuffer(this.timeStampBuffer));
			//logging
		
			updateBufferAfterSend(receiverIndex);
			}
			//System.out.println("after send"+mObj.getId()+this.processClock.getCurrentTime());
			// get the proxy object
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",
					4303);
			RMI reciever = (RMI) registry.lookup(Integer.toString(receiverIndex));
			
			// send the message
			this.messagesSent++;
			DelayedMessageSender sender = new DelayedMessageSender(this.processIndex, this.processClock.getCurrentTime(), reciever, mObj, 1000);
			new Thread(sender).start();
			
		}
		catch (Exception e)
		{
			//System.out.println("[" + this.processIndex + "]" + this.getProcessClock().getCurrentTime() + " recipient not found.");
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		while(this.messagesSent < 10)
		{
			for(int j = 0; j < this.processes; j++)
			{
				if (processIndex != j) {
					send("m", j);
				}
			}
		}
		while(true);
	}
	
	synchronized public void receive(Message message) throws RemoteException {
		// notify the clock
		
		System.out.println("[" + this.processIndex + "]" + this.processClock.getCurrentTime() + " receive from process "
				+ message.getSenderIndex() + " message: " + message);
		
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
				System.out.println("[" + this.processIndex + "]" + this.processClock.getCurrentTime() + " buffer message " + message);
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
			System.out.println("[" + this.processIndex + "]" + this.processClock.getCurrentTime() + " try to deliver message " + m);
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
		System.out.println("[" + this.processIndex + "]" + this.processClock.getCurrentTime() + " deliver "+m);
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
	
	public VectorClock getProcessClock()
	{
		return this.processClock;
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
