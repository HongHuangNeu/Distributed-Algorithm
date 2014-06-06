package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class Report extends Payload {
    private double bestWeight;

    public Report(int from, double bestWeight) {
        super(from);
        this.bestWeight = bestWeight;
    }

    public double getBestWeight() {
        return bestWeight;
    }

    @Override
    public String toString() {
        return "Report{" +
                "bestWeight" +
                "=" + bestWeight +
                '}';
    }
}
