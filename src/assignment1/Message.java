package assignment1;

import java.io.Serializable;

import assignment1.clock.TimeStamp;

public class Message<T> implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;
	private String senderName;
	private int senderIndex;
	private String message;
	private int id;
	private TimeStamp<T> sentAt;
	
	public Message(String senderName, int senderIndex, String message, int id, TimeStamp<T> sentAt) {
		this.senderName = senderName;
		this.senderIndex = senderIndex;
		this.message = message;
		this.id = id;
		this.sentAt = sentAt;
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
	
}
