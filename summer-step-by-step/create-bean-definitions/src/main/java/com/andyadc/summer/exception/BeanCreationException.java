package com.andyadc.summer.exception;

public class BeanCreationException extends BeansException {

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
