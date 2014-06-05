package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class Test extends Payload {
    private double identity;
    private int level;

    public Test(int from, double identity, int level) {
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
}
