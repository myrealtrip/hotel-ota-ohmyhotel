package com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception;

import lombok.Getter;

@Getter
public class MrtAgentException extends RuntimeException {

    protected final int status;

    public MrtAgentException(String message, int status) {
        super(message);
        this.status = status;
    }

    public MrtAgentException(Throwable throwable, int status) {
        super(throwable);
        this.status = status;
    }

    public MrtAgentException(String message, Throwable throwable, int status) {
        super(message, throwable);
        this.status = status;
    }
}
