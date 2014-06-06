package ghs.message;

import ghs.State;

/**
 * Created by ferdy on 6/4/14.
 */
public class Initiate extends Payload {
    private int level;
    private double core;
    private State search;

    public Initiate(int from, int level, double core, State search) {
        super(from);
        this.level = level;
        this.core = core;
        this.search = search;
    }

    public int getLevel() {
        return level;
    }

    public double getCore() {
        return core;
    }

    public State getState() {
        return search;
    }

    @Override
    public String toString() {
        return "Initiate{" +
                "level=" + level +
                ", core=" + core +
                ", search=" + search +
                '}';
    }
}
