package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Connect implements Payload {

    private int senderId;
	private int l;
	public Connect(int senderId, int l) {
        this.senderId = senderId;
        this.l = l;
    }

    public int getSenderId() {
        return this.senderId;
	}
	public int getL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
}
