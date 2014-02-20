package assignment1;

public class RemoteProcess implements RemoteProcess_RMI<VectorTimeStamp>{
	
	private VectorTimeStamp timeStamp;
	
	public RemoteProcess(int processes)
	{
		this.timeStamp = new VectorTimeStamp(processes);
	}
	
	public VectorTimeStamp getTime()
	{
		return this.timeStamp;
	}
	
	public void send(Object messange)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recieve(VectorTimeStamp timeStamp, Object message) {
		// TODO Auto-generated method stub
		
	}

}
