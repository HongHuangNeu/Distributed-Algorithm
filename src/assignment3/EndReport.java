package assignment3;

import java.util.Map;

public class EndReport extends Message{
	private float[] adjacent;
	private Map<String,Integer> m;
	public Map<String, Integer> getM() {
		return m;
	}
	public void setM(Map<String, Integer> m) {
		this.m = m;
	}
	public float[] getAdjacent() {
		return adjacent;
	}
	public void setAdjacent(float[] adjacent) {
		this.adjacent = adjacent;
	}
	public EndReport(int senderId,float[]adjacent, Map<String,Integer> m)
	{
		super(senderId);
		this.adjacent=adjacent;
		this.m=m;
	}
	@Override
	public String toString()
	{
		return "End REPORT";
	}
}
