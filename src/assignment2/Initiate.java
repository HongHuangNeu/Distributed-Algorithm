package assignment2;

public class Initiate extends Message{
private int L;
private float F;
private State S;
	public Initiate(int senderId,int L,float F,State S)
	{
		super(senderId);
		this.L=L;
		this.F=F;
		this.S=S;
	}
	public int getL() {
		return L;
	}
	public void setL(int l) {
		L = l;
	}
	public float getF() {
		return F;
	}
	public void setF(float f) {
		F = f;
	}
	public State getS() {
		return S;
	}
	public void setS(State s) {
		S = s;
	}
}
