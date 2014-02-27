package assignment1.clock;

import java.util.ArrayList;
import java.util.List;

public class VectorClock implements Clock<List<Integer>>
{
	private VectorTimeStamp currentTime;
	private int processId;
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

	public void updateRecieved( TimeStamp<List<Integer>> remoteTime)
	{
          		VectorTimeStamp ownTime=this.currentTime.inc(processId);
          		this.currentTime=ownTime.max(remoteTime);
	}

	public void updateOther(int processId, TimeStamp<List<Integer>> localTime, TimeStamp<List<Integer>> remoteTime)
	{
		this.currentTime = this.currentTime.inc(processId);
	}
}