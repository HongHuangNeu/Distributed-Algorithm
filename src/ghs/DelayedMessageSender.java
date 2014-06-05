package ghs;

import java.rmi.RemoteException;

import ghs.message.Message;
import ghs.clock.VectorTimeStamp;
import ghs.util.Main;

public class DelayedMessageSender implements Runnable {
    private int fromId;
    private RMI to;
    private Message m;
    private VectorTimeStamp t;

    private long maxDelay = 0;

    public DelayedMessageSender(int fromId, VectorTimeStamp t, RMI to, Message m, long maxDelay) {
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
                System.out.println("[" + this.fromId + "]" + this.t + " number before send" + (Main.id + 1));
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
