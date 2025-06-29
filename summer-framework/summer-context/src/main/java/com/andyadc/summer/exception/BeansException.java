package com.andyadc.summer.exception;

import java.io.Serial;

public class BeansException extends NestedRuntimeException {

    @Serial
    private static final long serialVersionUID = 1372323556495306865L;

    public BeansException() {
    }

    public BeansException(String message) {
        super(message);
    }

    public BeansException(Throwable cause) {
        super(cause);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }

}
