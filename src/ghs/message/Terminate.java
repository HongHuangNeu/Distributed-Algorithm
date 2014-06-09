package ghs.message;

/**
 * Created by ferdy on 6/6/14.
 */
public class Terminate extends Payload {
	private int from;
	public Terminate(int from) {
        super(from);
        this.from=from;
    }
	@Override
    public String toString() {
        return "Terminate from "+from;
    }
}
