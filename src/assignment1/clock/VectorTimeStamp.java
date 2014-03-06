package assignment1.clock;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VectorTimeStamp implements TimeStamp<List<Integer>>{

	private List<Integer> time;

	public VectorTimeStamp(int size)
	{
		this.time = new ArrayList<Integer>(size);

		for(int i = 0; i < size; i++){
			this.time.add(0);
		}
	}

	public VectorTimeStamp(List<Integer> times)
	{
		this.time = new ArrayList<Integer>(times);
	}

	public VectorTimeStamp(String serializedTimeStamp)
	{
		Pattern numbers = Pattern.compile("\\d+");
		Matcher m = numbers.matcher(serializedTimeStamp);
		ArrayList<Integer> times = new ArrayList<Integer>();

		while(m.find())
		{
			times.add(Integer.parseInt(m.group()));
		}
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

	public VectorTimeStamp max(TimeStamp<List<Integer>> other)
	{
		List<Integer> maxTimes = new ArrayList<Integer>(other.getTime());

		for(int i = 0; i < this.getTime().size(); i++)
		{
			maxTimes.set(i, Math.max(this.getTime().get(i), maxTimes.get(i)));
		}

		return new VectorTimeStamp(maxTimes);
	}
	
	@Override
	public String toString()
	{
		String s = "(";

		for(int i = 0; i < this.time.size() - 1; i++)
		{
			s += this.time.get(i) + ", ";
		}

		s += this.time.get(this.time.size() - 1);
		s += ")";

		return s;
	}
	
	//hong added
	public int compareTo(TimeStamp<List<Integer>> other)
	{
		List<Integer> maxTimes = new ArrayList<Integer>(other.getTime());
		for(int i=0;i<this.getTime().size();i++)
		{
			if(this.getTime().get(i)<maxTimes.get(i))
			{
				return -1;
			}
		}
		return 1;
	}
}