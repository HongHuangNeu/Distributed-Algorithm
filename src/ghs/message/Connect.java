package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class Connect extends Payload {
    private int level;

    public Connect(int from, int level) {
        super(from);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
