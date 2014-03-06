package assignment1.clock;

import java.io.Serializable;
import java.util.List;

public interface TimeStamp<T> extends Serializable {
	public T getTime();//List<Integer>
	public Boolean biggerThan(TimeStamp<List<Integer>> other);
	public VectorTimeStamp max(TimeStamp<List<Integer>> other);
}