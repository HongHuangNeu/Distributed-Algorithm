package ghs.message;

import java.io.Serializable;
import java.util.*;

import ghs.clock.VectorTimeStamp;

public class Message implements Serializable {
    private static final long serialVersionUID = -5836283489677344417L;

    private int senderId;
    private int receiverId;
    private VectorTimeStamp sentAt;
    private Map<Integer, VectorTimeStamp> buffer;
    private Payload payload;

    public Message(
            int senderId,
            int receiverId,
            VectorTimeStamp at,
            Map<Integer, VectorTimeStamp> buffer,
            Payload payload) {

        this.senderId = senderId;
        this.sentAt = at;
        this.receiverId = receiverId;
        this.buffer = buffer;
        this.payload = payload;
    }

    public int getSenderId() {
        return this.senderId;
    }

    public VectorTimeStamp getSentAt() {
        return this.sentAt;
    }

    public Map<Integer, VectorTimeStamp> getTimeStampBuffer() {
        return this.buffer;
    }

    public void setTimeStampBuffer(Map<Integer, VectorTimeStamp> buffer) {
        this.buffer = buffer;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public Payload getPayload() {
        return this.payload;
    }
}
