package com.myrealtrip.ohmyhotel.outbound.agent.mrt.exception;

import lombok.Getter;

@Getter
public class MrtAgent4xxException extends MrtAgentException {

    private final String code;

    public MrtAgent4xxException(String message, int status, String code) {
        super(message, status);
        this.code = code;
    }

    public MrtAgent4xxException(Throwable throwable, int status, String code) {
        super(throwable, status);
        this.code = code;
    }

    public MrtAgent4xxException(String message, Throwable throwable, int status, String code) {
        super(message, throwable, status);
        this.code = code;
    }
}
