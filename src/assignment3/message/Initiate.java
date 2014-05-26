package assignment3.message;

import java.util.Map;

import assignment3.State;
import assignment3.clock.VectorTimeStamp;

public class Initiate extends Message{
private int l;
private float f;
private State s;
	public Initiate(
			int senderId,VectorTimeStamp at,
			int receiverId,
			Map<Integer,VectorTimeStamp> buffer,
			int l,
			float f,
			State s) {
		
		super(senderId, at, receiverId, buffer);
		this.l=l;
		this.f=f;
		this.s=s;
	}
	public int getL() {
		return l;
	}
	public void setL(int l) {
		this.l = l;
	}
	public float getF() {
		return this.f;
	}
	public void setF(float f) {
		this.f = f;
	}
	public State getS() {
		return this.s;
	}
	public void setS(State s) {
		this.s = s;
	}
}
