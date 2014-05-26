package assignment3;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import assignment3.clock.VectorClock;
import assignment3.clock.VectorTimeStamp;
import assignment3.message.Message;
import assignment3.message.Payload;

//@SuppressWarnings("serial")
public class Process extends UnicastRemoteObject implements RMI,
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
		timeStampBuffer=new HashMap<Integer, VectorTimeStamp>();
		try {
			
			Registry registry = LocateRegistry.getRegistry(4303);
			registry.bind(Integer.toString(processIndex), this);
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
		}
		catch(AlreadyBoundException e)
		{
			e.printStackTrace();
		}
	}

	public void send(Payload p, int to)
	{
		try
		{
            Message m = new Message(this.processIndex, to, this.getProcessClock().getCurrentTime(), this.timeStampBuffer, p);
			// wrap message contents m into message object
			synchronized(this)
			{
                // notify the clock
                this.processClock.updateSent();

                updateBufferAfterSend(m.getReceiverId());
			}
			// get the process proxy
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",
					4303);
			RMI reciever = (RMI) registry.lookup(Integer.toString(m.getReceiverId()));
			
			// send the message
			this.messagesSent++;
			DelayedMessageSender sender = new DelayedMessageSender(this.processIndex, this.processClock.getCurrentTime(), reciever, m, 1000);
			new Thread(sender).start();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true);
	}
	
	synchronized public void receive(Message message) throws RemoteException {
		// notify the clock
		
		System.out.println("[" + this.processIndex + "]" + this.processClock.getCurrentTime() + " receive from process "
				+ message.getSenderId() + " message: " + message);
		
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
		this.processMessage(m.getPayload());
	}

    protected void processMessage(Payload p) {
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
	
	private Map<Integer, VectorTimeStamp> copyBuffer(Map<Integer,VectorTimeStamp> localBuffer)
	{
		Map<Integer, VectorTimeStamp> result=new HashMap<Integer, VectorTimeStamp>();
		for(Integer key:localBuffer.keySet())
		{
			result.put(key, new VectorTimeStamp(localBuffer.get(key).getTime()));
		}
		return result;
	}
	
}
