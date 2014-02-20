package assignment1;

public interface TimeStamp<T> {
	public T getTime();
	public TimeStamp<T> copy();
}
