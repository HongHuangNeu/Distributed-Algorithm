package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Reject extends Message{
	public Reject(
		int senderId,VectorTimeStamp at,
		int receiverId,
		Map<Integer,VectorTimeStamp> buffer)
	{
		super(senderId, at, receiverId, buffer);
	}
}
