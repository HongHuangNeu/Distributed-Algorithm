package assignment3.message;

import java.io.Serializable;
import java.util.*;

import assignment1.clock.VectorTimeStamp;

public class Message implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;
    public  int seq_id;
    public boolean inQueue=false;
	private int senderId;
	public Message(int senderId)
	{
		this.senderId=senderId;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
