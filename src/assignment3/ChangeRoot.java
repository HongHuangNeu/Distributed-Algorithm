package assignment3;

public class ChangeRoot extends Message{
	public ChangeRoot(int senderId)
	{
		super(senderId);
	}
	@Override
	public String toString()
	{
		return "ChangeRoot";
	}
}
