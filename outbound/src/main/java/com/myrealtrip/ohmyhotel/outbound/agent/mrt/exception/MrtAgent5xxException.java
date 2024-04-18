package com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception;

import lombok.Getter;

@Getter
public class MrtAgent5xxException extends MrtAgentException {

    private final String code;

    public MrtAgent5xxException(String message, int status, String code) {
        super(message, status);
        this.code = code;
    }

    public MrtAgent5xxException(Throwable throwable, int status, String code) {
        super(throwable, status);
        this.code = code;
    }

    public MrtAgent5xxException(String message, Throwable throwable, int status, String code) {
        super(message, throwable, status);
        this.code = code;
    }
}
