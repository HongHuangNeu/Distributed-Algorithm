package assignment3;

public class CheckTerminate extends Message{
	public CheckTerminate(int senderId)
	{
		super(senderId);
	}
	@Override
	public String toString()
	{
		return "terminate";
	}
}
