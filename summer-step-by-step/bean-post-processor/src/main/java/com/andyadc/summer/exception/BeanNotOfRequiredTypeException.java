package com.andyadc.summer.exception;

public class BeanNotOfRequiredTypeException extends BeansException {

    private static final long serialVersionUID = -2906115359485853864L;

    public BeanNotOfRequiredTypeException() {
    }

    public BeanNotOfRequiredTypeException(String message) {
        super(message);
    }

}
