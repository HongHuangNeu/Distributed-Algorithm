package assignment3;

public class Reject extends Message{
	public Reject(int senderId)
	{
		super(senderId);
	}
	@Override
	public String toString()
	{
		return "Reject";
	}
}
