package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;
import assignment3.message.Message;

public class CheckTerminate implements Payload {
    private int senderId;

	public CheckTerminate(int senderId) {
        this.senderId = senderId;
    }

    public int getSenderId() {
        return this.senderId;
    }
}
