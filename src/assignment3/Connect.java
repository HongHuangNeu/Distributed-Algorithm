package assignment3;

public class Connect extends Message{
	private int L;
	public Connect(int senderId,int L)
	{
		super(senderId);
		this.L=L;
	}
	public int getL() {
		return L;
	}
	public void setL(int l) {
		L = l;
	}
}
