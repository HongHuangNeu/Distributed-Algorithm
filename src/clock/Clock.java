package clock;

import assignment1.TimeStamp;

public interface Clock<T extends TimeStamp<?>> {
	public void updateSend();
	public void updateRecieve();
	public T getCurrentTime();
}
