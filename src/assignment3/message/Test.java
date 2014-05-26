package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Test implements Payload {
    private int senderId;
    private int L;
    private float F;
	public Test(int senderId, int l, float f) {
        this.senderId = senderId;
    }

    public int getSenderId() {
        return this.senderId;
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
