package ghs.message;

/**
 * Created by ferdy on 6/6/14.
 */
public class LogMessage extends Payload {
    private String logMessage;

    public LogMessage(int from, String logMessage) {
        super(from);
        this.logMessage = logMessage;
    }

    public String getLogMessage() {
        return logMessage;
    }

    @Override
    public String toString() {
        return logMessage;
    }
}
