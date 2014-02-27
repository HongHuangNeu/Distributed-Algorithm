package assignment1;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;
	private String senderName;
	private String message;
	private int id;
	public Message(String senderName, String message, int id) {
		this.senderName = senderName;
		this.message = message;
		this.id = id;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public int getId()
	{
		return this.id;
	}
	
}
