package assignment1;

public class RemoteProcess implements RemoteProcess_RMI<VectorTimeStamp>{
	
	private VectorTimeStamp timeStamp;
	private String processId;
	
	public RemoteProcess(int processes, int processId)
	{
		this.timeStamp = new VectorTimeStamp(processes);
		this.processId = processId;
	}
	
	public VectorTimeStamp getTime()
	{
		return this.timeStamp;
	}

        public String getId()
        {
            return this.Id;
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
