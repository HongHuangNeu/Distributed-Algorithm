package ghs;

import ghs.clock.VectorTimeStamp;
import ghs.message.Message;
import ghs.rmi.MessageReciever;

import java.rmi.RemoteException;

public class DelayedMessageSender implements Runnable {
    private int fromId;
    private MessageReciever to;
    private Message m;
    private VectorTimeStamp t;

    private long maxDelay = 0;

    public DelayedMessageSender(int fromId, VectorTimeStamp t, MessageReciever to, Message m, long maxDelay) {
        this.fromId = fromId;
        this.to = to;
        this.t = t;
        this.m = m;
        this.maxDelay = maxDelay;
    }

    public void run() {
        long delay = this.generateDelay();

        try {
            Thread.sleep(delay);

            synchronized (this.to) {
                to.receive(this.m);
            }
        } catch (InterruptedException e) {
            // Can't sleep!
            e.printStackTrace();
        } catch (RemoteException e) {
            // Remote exception
            e.printStackTrace();
        }


    }

    private long generateDelay() {
        return Math.round(Math.random() * this.maxDelay);
    }
}
