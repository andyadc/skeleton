package com.andyadc.summer.exception;

public class NestedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -4748330045161022996L;

    public NestedRuntimeException() {
    }

    public NestedRuntimeException(String message) {
        super(message);
    }

    public NestedRuntimeException(Throwable cause) {
        super(cause);
    }

    public NestedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
