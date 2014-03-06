package assignment1.clock;

import java.io.Serializable;
import java.util.List;

public interface TimeStamp<T> extends Serializable, Comparable<TimeStamp<T>> {
	public T getTime();//List<Integer>
	public VectorTimeStamp max(TimeStamp<List<Integer>> other);
	public VectorTimeStamp inc(int processId);
}