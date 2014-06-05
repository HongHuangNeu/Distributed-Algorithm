package ghs.message;

import java.io.Serializable;

/**
 * Created by ferdy on 5/26/14.
 */
abstract public class Payload implements Serializable {
    private int from;

    public Payload(int from) {
        this.from = from;
    }

    public int getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "from=" + from +
                '}';
    }
}
