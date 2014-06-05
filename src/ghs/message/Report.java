package ghs.message;

import ghs.Edge;

import java.util.Optional;

/**
 * Created by ferdy on 6/4/14.
 */
public class Report extends Payload {
    private Edge bestEdge;

    public Report(int from, Edge minOutEdge) {
        super(from);
        this.bestEdge = minOutEdge;
    }

    public Edge getBestEdge() {
        return bestEdge;
    }
}
