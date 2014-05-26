package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Connect extends Message{
	private int l;
	public Connect(
			int senderId,VectorTimeStamp at,
			int receiverId,
			Map<Integer,VectorTimeStamp> buffer, int l)
	{
		super(senderId, at, receiverId, buffer);
		this.l=l;
	}
	public int getL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
}
