package assignment3;

public class Test extends Message{
private int L;
private float F;
	public Test(int senderId, int L, float F)
	{
		super(senderId);
		this.L=L;
		this.F=F;
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
	@Override
	public String toString()
	{
		return "Test";
	}
}
