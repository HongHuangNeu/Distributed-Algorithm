package ghs;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by ferdy on 6/4/14.
 */
public class Edge implements Comparable<Edge> {
    private int u;
    private int v;

    private double c;

    private EdgeType type;

    public Edge(int u, int v, double c) {
        this.u = u;
        this.v = v;
        this.c = c;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public double getC() {
        return c;
    }

    public EdgeType getType() {
        return type;
    }

    public void setType(EdgeType type) {
        this.type = type;
    }

    @Override
    public int compareTo(Edge o) {
        return (this.getC() > o.getC()) ? 1 : -1;
    }

    public static void main(String[] args) {
        Edge e1 = new Edge(0, 0, 1);
        Edge e2 = new Edge(0, 0, 2);

        LinkedList<Edge> edges = new LinkedList<Edge>();

        edges.add(e1);
        edges.add(e2);

        System.out.println(edges.stream().min(Comparator.<Edge>naturalOrder()));
        System.out.println(e1.compareTo(e2));

    }
}
