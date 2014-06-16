package assignment3;

import java.util.Map;

public class MessageCount extends Message {
	private Map<String,Integer> m;
	public MessageCount(int senderId,Map<String,Integer> m)
	{
		super(senderId);
		this.m=m;
	}
}
