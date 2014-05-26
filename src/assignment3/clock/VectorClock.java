package assignment3.clock;

import java.util.ArrayList;
import java.util.List;

public class VectorClock 
{
	private VectorTimeStamp currentTime;
	private int processId;
	public VectorClock(int nProcesses,int processId)
	{
		this.currentTime = new VectorTimeStamp(nProcesses);
		this.processId=processId;
	}

	public VectorTimeStamp getCurrentTime()
	{
		return new VectorTimeStamp(this.currentTime.getTime());
	}

	public void updateSent()
	{
		this.currentTime = this.currentTime.inc(processId);
	}

	public void updateRecieved( VectorTimeStamp remoteTime)
	{
          		VectorTimeStamp ownTime=this.currentTime.inc(processId);
          		this.currentTime=ownTime.max(remoteTime);
	}

	public void updateOther(int processId, VectorTimeStamp localTime, VectorTimeStamp remoteTime)
	{
		this.currentTime = this.currentTime.inc(processId);
	}

	public void setCurrentTime(VectorTimeStamp currentTime) {
		this.currentTime = currentTime;
	}
	public VectorTimeStamp getIncTimeStamp()
	{
		return this.currentTime.inc(processId);
	}
}