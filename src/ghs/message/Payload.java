package ghs.message;

/**
 * Created by ferdy on 5/26/14.
 */
abstract public class Payload {
    private int from;

    public Payload(int from) {
        this.from = from;
    }

    public int getFrom() {
        return from;
    }
}
