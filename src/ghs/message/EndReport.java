package ghs.message;

/**
 * Created by ferdy on 6/6/14.
 */
public class EndReport extends Payload {
    private double[] adjacents;

    public EndReport(int from, double[] adjacents) {
        super(from);
        this.adjacents = adjacents;
    }

    public double[] getAdjacents() {
        return adjacents;
    }
}
