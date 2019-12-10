package com.kanven.netty;

import java.io.Serializable;

public class Protocol implements Serializable {

    private static final long serialVersionUID = 6457472033930708474L;

    private long seq;

    private Object body;

    private long sessionId = -1;

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "seq=" + seq +
                ", body=" + body +
                ", sessionId=" + sessionId +
                '}';
    }

}
