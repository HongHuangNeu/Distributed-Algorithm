
package assignment1;
import java.rmi.RemoteException;
import java.util.*;

import assignment1.clock.Clock;
import assignment1.clock.TimeStamp;
import assignment1.clock.VectorTimeStamp;
public class Deliver<T> extends Process<T> {
	private static final long serialVersionUID = 7247714666080613254L;
	public ArrayList<Message<T>> MessageBuffer=new ArrayList<Message<T>>();
	public Sender<T> processor;
	public Deliver(String processName, Sender<T> sender) throws RemoteException
	{
		super(processName,sender.getProcessIndex(),sender.getClock());
		this.processor=sender;
	    super.setClock(sender.getClock());
	}
	public void addToMessage(Message<T> message)
	{
		
         MessageBuffer.add(message);
         for(Message<T> m:MessageBuffer)
         {
        	 if(determineDeliverable(m))
        	 {
             processor.getMessage(m);
             for(Message<T> fm:this.MessageBuffer)
             {
            	 if(determineDeliverable(fm))
            	 {
            		 processor.getMessage(fm);
            	 }
             }
        	 }
         }
	}
	public boolean determineDeliverable(Message<T> message)
	{
		Map<Integer,VectorTimeStamp> buffer=message.getTimeStampBuffer();
		TimeStamp myTime=this.processClock.getCurrentTime();
        if(!buffer.containsKey(this.processIndex))
        {
        	return true;
        }
        else if(!buffer.get(this.processIndex).BiggerThan(myTime)){
       
        return true;
       
        }
		return false;
        
	}
}
