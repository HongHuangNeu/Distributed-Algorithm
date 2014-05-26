package assignment3.message;

import java.util.Map;

import assignment3.clock.VectorTimeStamp;

public class Report extends Message{
	private float best_weight;
	public Report(
			int senderId,VectorTimeStamp at,
			int receiverId,
			Map<Integer,VectorTimeStamp> buffer,
			float best_weight)
	{
		super(senderId, at, receiverId, buffer);
		
		this.best_weight=best_weight;
	}
	public float getBest_weight() {
		return best_weight;
	}
	public void setBest_weight(float best_weight) {
		this.best_weight = best_weight;
	}
}
