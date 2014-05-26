package assignment3.message;

import java.io.Serializable;
import java.util.*;

import assignment3.clock.VectorTimeStamp;

public class Message implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;

	private int senderId;
	private int receiverId;
	private VectorTimeStamp sentAt;
	private Map<Integer,VectorTimeStamp> buffer;
	
	public Message(
		int senderId,
		VectorTimeStamp at,
		int receiverId,
		Map<Integer,VectorTimeStamp> buffer) {
		
		this.senderId = senderId;
		this.sentAt = at;
		this.receiverId=receiverId;
		this.buffer=buffer;
	}
	
	public int getSenderId()
	{
		return this.senderId;
	}
	
	public VectorTimeStamp getSentAt()
	{
		return this.sentAt;
	}
	public Map<Integer, VectorTimeStamp> getTimeStampBuffer() {
		return this.buffer;
	}

	public void setTimeStampBuffer(Map<Integer, VectorTimeStamp> buffer) {
		this.buffer = buffer;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
}
