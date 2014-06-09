package ghs.message;

import java.util.List;

/**
 * Created by ferdy on 6/6/14.
 */
public class EndReport extends Payload {
    private double[] inMST;
    private int from;
    public EndReport(int from, double[] inMST) {
        super(from);
        this.from=from;
        this.inMST = inMST;
    }

    public double[] getAdjacents() {
        return inMST;
    }
    @Override
    public String toString() {
        return "EndReport from "+from;
    }
}
