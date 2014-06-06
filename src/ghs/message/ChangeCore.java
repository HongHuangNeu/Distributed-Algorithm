package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class ChangeCore extends Payload {
    public ChangeCore(int from) {
        super(from);
    }

    @Override
    public String toString() {
        return "ChangeCore{" +
                '}';
    }
}
