package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;
import assignment3.message.Message;

public class Accept implements Payload{
	public static int Initial=-1;
    private int senderId;
	public Accept(int senderId) {
        this.senderId = senderId;
	}

    public int getSenderId() {
        return this.senderId;
    }
}
