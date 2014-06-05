package ghs;

/**
 * Created by ferdy on 6/4/14.
 */
public class Edge implements Comparable<Edge> {
    private int u;
    private int v;

    private double w;

    private EdgeType type;

    public Edge(int u, int v, double c) {
        this.u = u;
        this.v = v;
        this.w = c;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public double getW() {
        return w;
    }

    public EdgeType getType() {
        return type;
    }

    public void setType(EdgeType type) {
        this.type = type;
    }

    @Override
    public int compareTo(Edge o) {
        return (this.getW() > o.getW()) ? 1 : -1;
    }
}
