package assignment1.clock;

import java.io.Serializable;

public interface TimeStamp<T> extends Serializable {
	public T getTime();
}