package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;
import assignment3.message.Message;

public class Accept extends Message{
	public static int Initial=-1;
	public Accept(
		int senderId,VectorTimeStamp at,
		int receiverId,
		Map<Integer,VectorTimeStamp> buffer) {
		
		super(senderId, at, receiverId, buffer);
	}
}
