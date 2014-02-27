package assignment1.clock;

public interface Clock<T>
{
	public TimeStamp<T> getCurrentTime();

	public void updateSent(int processId);
	public void updateRecieved(TimeStamp<T> localTime, TimeStamp<T> remoteTime);
	public void updateOther(int processId, TimeStamp<T> localTime, TimeStamp<T> remoteTime);
}
