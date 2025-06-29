package com.andyadc.summer.exception;

import java.io.Serial;

public class BeanCreationException extends BeansException {

    @Serial
    private static final long serialVersionUID = -6478941217222955753L;

    public BeanCreationException() {
    }

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCreationException(Throwable cause) {
        super(cause);
    }

}
