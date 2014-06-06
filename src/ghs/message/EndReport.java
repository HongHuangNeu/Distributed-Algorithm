package ghs.message;

/**
 * Created by ferdy on 6/6/14.
 */
public class EndReport extends Payload {
    private int[] adjacents;

    public EndReport(int from, int[] adjacents) {
        super(from);
        this.adjacents = adjacents;
    }

    public int[] getAdjacents() {
        return adjacents;
    }
}
