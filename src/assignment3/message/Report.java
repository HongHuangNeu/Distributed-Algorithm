package assignment3.message;

public class Report implements Payload {
    private int senderId;
	private float bestWeight;

	public Report(int senderId, float bestWeight) {
        this.senderId = senderId;
        this.bestWeight = bestWeight;
    }

    public int getSenderId() {
        return this.senderId;
    }
	public float getBestWeight() {
		return bestWeight;
	}
	public void setBestWeight(float bestWeight) {
		this.bestWeight = bestWeight;
	}
}
