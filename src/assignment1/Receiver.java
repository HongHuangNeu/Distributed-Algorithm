
package assignment1;
import java.rmi.RemoteException;
import java.util.*;

import assignment1.clock.Clock;
import assignment1.clock.TimeStamp;
import assignment1.clock.VectorTimeStamp;
public class Receiver extends Process<List<Integer>> {
	private static final long serialVersionUID = 7247714666080613254L;
	public ArrayList<Message<List<Integer>>> MessageBuffer=new ArrayList<Message<List<Integer>>>();
	public Sender processor;
	public Receiver(String processName, Sender sender) throws RemoteException
	{
		super(processName,sender.getProcessIndex(),sender.getClock());
		this.processor=sender;
	    super.setClock(sender.getClock());
	}
	public void addToMessage(Message<List<Integer>> message)
	{
		
         MessageBuffer.add(message);
         for(Message<List<Integer>> m:MessageBuffer)
         {
        	 if(determineDeliverable(m))
        	 {
	             processor.getMessage(m);
	             for(Message<List<Integer>> fm:this.MessageBuffer)
	             {
	            	 if(determineDeliverable(fm))
	            	 {
	            		 processor.getMessage(fm);
	            	 }
	             }
        	 }
         }
	}
	public boolean determineDeliverable(Message<List<Integer>> message)
	{
		Map<Integer,TimeStamp<List<Integer>>> buffer=message.getTimeStampBuffer();
		TimeStamp myTime=this.processClock.getCurrentTime();
        if(!buffer.containsKey(this.processIndex))
        {
        	return true;
        }
        else if(!buffer.get(this.processIndex).biggerThan(myTime)){
       
        return true;
       
        }
		return false;
        
	}
}
