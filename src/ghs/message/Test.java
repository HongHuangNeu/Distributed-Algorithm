package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class Test extends Payload {
    private int level;
    private double identity;

    public Test(int from, int level, double identity) {
        super(from);
        this.identity = identity;
        this.level = level;
    }

    public double getIdentity() {
        return identity;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "Test{" +
                "identity=" + identity +
                ", level=" + level +
                '}';
    }
}
