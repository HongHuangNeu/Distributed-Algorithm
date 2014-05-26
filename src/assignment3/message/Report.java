package assignment3.message;

public class Report extends Message{
	private float best_weight;
	public Report(int senderId,float best_weight)
	{
		super(senderId);
		this.best_weight=best_weight;
	}
	public float getBest_weight() {
		return best_weight;
	}
	public void setBest_weight(float best_weight) {
		this.best_weight = best_weight;
	}
}
