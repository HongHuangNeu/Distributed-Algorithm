package assignment2;

public class Request extends Message {

	private int numOfRequest;
	private int senderId;

	public Request(int senderId, int numOfRequest) {
		this.senderId = senderId;
		this.numOfRequest = numOfRequest;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public Request(int i) {
		this.numOfRequest = i;
	}

	public int getNumOfRequest() {
		return numOfRequest;
	}

	public void setNumOfRequest(int numOfRequest) {
		this.numOfRequest = numOfRequest;
	}
}
