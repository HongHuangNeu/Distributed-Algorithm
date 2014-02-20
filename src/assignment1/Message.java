package assignment1;

public class Message<T extends TimeStamp<?>> {

	/**
	 * @param args
	 */
	
	RemoteProcess sender;
	String receiverId;
	T timeStamp;
    String message="hello";
	public RemoteProcess getSender()
	{return sender;}
	public String getReceiverName()
	{
		return receiverId;
	}
	public T getTimeStamp()
	{return timeStamp;}
	public String getMessage()
	{
		return this.message;
	}
	public void  setMessage(String s)
	{
		this.message=s;
	}
}
