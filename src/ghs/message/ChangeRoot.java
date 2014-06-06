package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class ChangeRoot extends Payload {
    public ChangeRoot(int from) {
        super(from);
    }

    @Override
    public String toString() {
        return "ChangeRoot{" +
                '}';
    }
}
