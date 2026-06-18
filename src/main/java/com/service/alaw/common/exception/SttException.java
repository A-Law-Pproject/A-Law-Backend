package com.service.alaw.common.exception;

public class SttException extends RuntimeException {

    public SttException(String message) {
        super(message);
    }

    public SttException(String message, Throwable cause) {
        super(message, cause);
    }
}
