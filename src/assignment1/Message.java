package assignment1;

import java.io.Serializable;
import java.util.*;

import assignment1.clock.TimeStamp;
import assignment1.clock.VectorTimeStamp;

public class Message<T> implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;

	private int senderIndex;
	private int receiverIndex;
	private String message;
	private int id;
	private TimeStamp<T> sentAt;
	private Map<Integer,TimeStamp<T>> TimeStampBuffer;
	
	public Message( int senderIndex, String message, int id, TimeStamp<T> sentAt,int receiverIndex,Map<Integer,TimeStamp<T>> timeStampBuffer) {
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
	
	public TimeStamp<T> getSentAt()
	{
		return this.sentAt;
	}
	public Map<Integer, TimeStamp<T>> getTimeStampBuffer() {
		return TimeStampBuffer;
	}

	public void setTimeStampBuffer(Map<Integer, TimeStamp<T>> timeStampBuffer) {
		TimeStampBuffer = timeStampBuffer;
	}

	public int getReceiverIndex() {
		return receiverIndex;
	}

	public void setReceiverIndex(int receiverIndex) {
		this.receiverIndex = receiverIndex;
	}

}
