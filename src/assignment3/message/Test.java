package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Test extends Message{
private int L;
private float F;
	public Test(
			int senderId,VectorTimeStamp at,
			int receiverId,
			Map<Integer,VectorTimeStamp> buffer,
			int L,
			float F)
	{
		super(senderId, at, receiverId, buffer);
		this.L=L;
		this.F=F;
	}
	public int getL() {
		return L;
	}
	public void setL(int l) {
		L = l;
	}
	public float getF() {
		return F;
	}
	public void setF(float f) {
		F = f;
	}
}
