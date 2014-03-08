package assignment2;

public class Token extends Message{
	private State[] TS;
	private int[] TN;
	public Token(int totalNum)
	{
		TS=new State[totalNum];
		TN=new int[totalNum];
		for(int i=0;i<totalNum;i++)
		{
			TS[i]=State.Other;
			TN[i]=0;
		}
	}
	public State[] getTS() {
		return TS;
	}
	public void setTS(State[] tS) {
		TS = tS;
	}
	public int[] getTN() {
		return TN;
	}
	public void setTN(int[] tN) {
		TN = tN;
	} 
	public void setTSelement(int index,State state)
	{
		TS[index]=state;
	}
	public void setTNelement(int index, int n)
	{
		TN[index]=n;
	}
}
