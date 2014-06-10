package assignment3;

public class EndReport extends Message{
	private float[] adjacent;
	public float[] getAdjacent() {
		return adjacent;
	}
	public void setAdjacent(float[] adjacent) {
		this.adjacent = adjacent;
	}
	public EndReport(int senderId,float[]adjacent)
	{
		super(senderId);
		this.adjacent=adjacent;
	}
}
