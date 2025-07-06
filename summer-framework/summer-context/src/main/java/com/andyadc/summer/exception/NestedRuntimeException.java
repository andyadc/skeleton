package com.andyadc.summer.exception;

import java.io.Serial;

public class NestedRuntimeException extends RuntimeException {

    @Serial
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
