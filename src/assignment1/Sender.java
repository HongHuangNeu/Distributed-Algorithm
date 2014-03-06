package assignment1;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

import assignment1.clock.Clock;
import assignment1.clock.TimeStamp;
import assignment1.clock.VectorTimeStamp;

public class Sender<T> extends Process<T> {
	private static final long serialVersionUID = 7247714666080613254L;
	private Map<Integer,VectorTimeStamp> TimeStampBuffer;
	public Sender(String processName, int processIndex, Clock<T> clock) throws RemoteException
	{
		super(processName,processIndex,clock);
		
	}
	public void getMessage(Message<T> message)
	{
	    this.processClock.updateRecieved(message.getSentAt());
	    updateBufferReceive(message.getTimeStampBuffer()); 
	}
	public void sendMessage(Message<T> message)
	{
		
		this.processClock.getIncTimeStamp();
		this.processClock.updateSent(this.processIndex);
		
	}
	public void updateBufferReceive(Map<Integer,VectorTimeStamp> MessageStampBuffer)
	{
		Iterator iter = MessageStampBuffer.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry<Integer,VectorTimeStamp> entry = (Map.Entry<Integer,VectorTimeStamp>) iter.next(); 
		    Integer key = entry.getKey(); 
		    VectorTimeStamp val = entry.getValue();
		    if(this.TimeStampBuffer.containsKey(key))
		    {
		    	VectorTimeStamp myVal=this.TimeStampBuffer.get(key);
		    	VectorTimeStamp messageVal=MessageStampBuffer.get(key);
		    	this.TimeStampBuffer.put(key, myVal.max(messageVal));
		    }
		    else{
		    	this.TimeStampBuffer.put(key, val);
		    }
		} 
	}
	
}
