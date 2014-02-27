package process.clock;

import java.util.List;

public class VectorClock implements Clock<List<Integer>>
{
	private VectorTimeStamp currentTime;
	
	public VectorClock(int nProcesses)
	{
		this.currentTime = new VectorTimeStamp(nProcesses);
	}
	
	public VectorTimeStamp getCurrentTime()
	{
		return this.currentTime;
	}
	
	public void updateSent(int processId)
	{
		this.currentTime = this.currentTime.inc(processId);
	}
	
	public void updateRecieved(TimeStamp<List<Integer>> localTime, TimeStamp<List<Integer>> remoteTime)
	{
		
	}
	
	public void updateOther(int processId, TimeStamp<List<Integer>> localTime, TimeStamp<List<Integer>> remoteTime)
	{
		this.currentTime = this.currentTime.inc(processId);
	}
}
