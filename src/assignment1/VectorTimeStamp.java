package assignment1;

import java.util.ArrayList;
import java.util.List;

public class VectorTimeStamp implements TimeStamp<List<Integer>>{
	
	private List<Integer> timeStamp;
	
	public VectorTimeStamp(int size)
	{
		this.timeStamp = new ArrayList<Integer>(size);
		
		for(int i = 0; i < size; i++)
		{
			this.timeStamp.set(i, 0);
		}
	}
	
	public VectorTimeStamp(List<Integer> times)
	{
		this.timeStamp = new ArrayList<Integer>(times);
	}
	
	public List<Integer> getTime() {
		return this.timeStamp;
	}
	
	public VectorTimeStamp copy()
	{
		return new VectorTimeStamp(this.getTime());
	}
}