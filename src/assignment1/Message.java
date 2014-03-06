package assignment1;

import java.io.Serializable;
import java.util.*;

import assignment1.clock.TimeStamp;
import assignment1.clock.VectorTimeStamp;

public class Message<T> implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;
	private String senderName;
	private int senderIndex;
	private String receiverName;
	private String message;
	private int id;
	private TimeStamp<T> sentAt;
	private Map<Integer,TimeStamp<List<Integer>>> TimeStampBuffer;
	
	public Message(String senderName, int senderIndex, String message, int id, TimeStamp<T> sentAt,String receiverName) {
		this.senderName = senderName;
		this.senderIndex = senderIndex;
		this.message = message;
		this.id = id;
		this.sentAt = sentAt;
		this.receiverName=receiverName;
	}
	
	public String getSenderName()
	{
		return this.senderName;
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
	public Map<Integer, TimeStamp<List<Integer>>> getTimeStampBuffer() {
		return TimeStampBuffer;
	}

	public void setTimeStampBuffer(Map<Integer, TimeStamp<List<Integer>>> timeStampBuffer) {
		TimeStampBuffer = timeStampBuffer;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

}
