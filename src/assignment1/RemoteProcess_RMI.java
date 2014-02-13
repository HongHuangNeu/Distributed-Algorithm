package assignment1;

public interface RemoteProcess_RMI<T extends TimeStamp>{
	public T getTime();
	
	public void send(Object messange);
}
