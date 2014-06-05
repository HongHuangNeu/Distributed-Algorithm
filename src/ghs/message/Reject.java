package ghs.message;

/**
 * Created by ferdy on 6/4/14.
 */
public class Reject extends Payload {
    public Reject(int from) {
        super(from);
    }


    @Override
    public String toString() {
        return "Reject{" +
                '}';
    }
}
