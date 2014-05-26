package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Reject implements Payload {

    private int senderId;

	public Reject(int senderId) {
        this.senderId = senderId;
    }

    public int getSenderId() {
        return this.senderId;
    }
}
