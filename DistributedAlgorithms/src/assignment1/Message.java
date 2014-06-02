package assignment1;

import java.io.Serializable;
import java.util.*;

import assignment1.clock.VectorTimeStamp;

public class Message implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;

	private int senderIndex;
	private int receiverIndex;
	private String message;
	private int id;
	private VectorTimeStamp sentAt;
	private Map<Integer,VectorTimeStamp> TimeStampBuffer;
	
	public Message( int senderIndex, String message, int id, VectorTimeStamp sentAt,int receiverIndex,Map<Integer,VectorTimeStamp> timeStampBuffer) {
		this.senderIndex = senderIndex;
		this.message = message;
		this.id = id;
		this.sentAt = sentAt;
		this.receiverIndex=receiverIndex;
		this.TimeStampBuffer=timeStampBuffer;
	}
	
	
	
	public int getSenderIndex()
	{
		return this.senderIndex;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public VectorTimeStamp getSentAt()
	{
		return this.sentAt;
	}
	public Map<Integer, VectorTimeStamp> getTimeStampBuffer() {
		return TimeStampBuffer;
	}

	public void setTimeStampBuffer(Map<Integer, VectorTimeStamp> timeStampBuffer) {
		TimeStampBuffer = timeStampBuffer;
	}

	public int getReceiverIndex() {
		return receiverIndex;
	}

	public void setReceiverIndex(int receiverIndex) {
		this.receiverIndex = receiverIndex;
	}
	public String toString()
	{
		String s = "";

		s+=this.id + " ";
		s+=this.sentAt;

		return s;
	}
}
