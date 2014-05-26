package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;
import assignment3.message.Message;

public class ChangeRoot implements Payload {
    private int senderId;
	public ChangeRoot(int senderId) {
        this.senderId = senderId;
    }

    public int getSenderId() {
        return this.senderId;
    }
}
