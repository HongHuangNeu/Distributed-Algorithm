package assignment1;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -5836283489677344417L;
	public String senderName;
	public String message;
	public int id;

	public Message(String senderName, String message, int id) {
		this.senderName = senderName;
		this.message = message;
		this.id = id;
	}
}
