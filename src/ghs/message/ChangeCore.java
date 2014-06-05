package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class ChangeCore extends Payload {
    private double core;

    private int level;

    public ChangeCore(int from, double core, int level) {
        super(from);
        this.core = core;
        this.level = level;
    }

    public double getCore() {
        return core;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "ChangeCore{" +
                "core=" + core +
                ", level=" + level +
                '}';
    }
}
