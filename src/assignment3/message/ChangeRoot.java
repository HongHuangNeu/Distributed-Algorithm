package assignment3.message;

import java.util.Map;

import assignment1.clock.VectorTimeStamp;
import assignment3.message.Message;

public class ChangeRoot extends Message{
	public ChangeRoot(
			int senderId,VectorTimeStamp at,
			int receiverId,
			Map<Integer,VectorTimeStamp> buffer)
	{
		super(senderId, at, receiverId, buffer);
	}
}
