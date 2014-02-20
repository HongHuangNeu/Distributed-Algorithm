package assignment1;

public class RemoteProcess implements RemoteProcess_RMI<VectorTimeStamp>{
	
	private VectorTimeStamp timeStamp;
	private int processId;
	
	public RemoteProcess(int processes, int processId)
	{
		this.timeStamp = new VectorTimeStamp(processes);
		this.processId = processId;
	}
	
	public VectorTimeStamp getTime()
	{
		return this.timeStamp;
	}

        public int getId()
        {
            return this.processId;
        }
        
	public void send(Message<VectorTimeStamp> message)
	{
		// TODO Auto-generated method stub
		synchronized(this)
		{
			System.out.println("Synchronize part of send, process id "+this.processId);
		}
		Delay delay=new Delay(message);
		new Thread(delay).start();
	}

	@Override
	public void recieve(Message<VectorTimeStamp> message) {
	
		// TODO Auto-generated method stub
		
		for(int i=0;i<100;i++){
			synchronized(this)
			{   
				System.out.println("Synchronize part of receive, process id "+this.processId+" run"+i);
			}
		}
	}

}
