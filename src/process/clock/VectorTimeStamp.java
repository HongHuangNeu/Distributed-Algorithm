package process.clock;

import java.util.ArrayList;
import java.util.List;

public class VectorTimeStamp implements TimeStamp<List<Integer>> {
	
	private List<Integer> time;

	public VectorTimeStamp(int size)
	{
		this.time = new ArrayList<Integer>(size);
		
		for(int i = 0; i < size; i++){
			this.time.set(i, 0);
		}
	}

	public VectorTimeStamp(List<Integer> times)
	{
		this.time = new ArrayList<Integer>(times);
	}
	
	public List<Integer> getTime()
	{
		return this.time;
	}
	
	public VectorTimeStamp inc(int i)
	{
		List<Integer> newTimes = new ArrayList<Integer>(this.time);
		int curTime = newTimes.get(i);
		newTimes.set(i, ++curTime);
		
		return new VectorTimeStamp(newTimes);
	}
	
	public VectorTimeStamp set(int i, int time)
	{
		List<Integer> newTimes = new ArrayList<Integer>(this.time);
		newTimes.set(i, time);
		
		return new VectorTimeStamp(newTimes);
	}
	
	public VectorTimeStamp max(VectorTimeStamp other)
	{
		List<Integer> maxTimes = new ArrayList<Integer>(other.getTime());
		
		for(int i = 0; i < this.getTime().size(); i++)
		{
			maxTimes.set(i, Math.max(this.getTime().get(i), maxTimes.get(i)));
		}
		
		return new VectorTimeStamp(maxTimes);
	}
}