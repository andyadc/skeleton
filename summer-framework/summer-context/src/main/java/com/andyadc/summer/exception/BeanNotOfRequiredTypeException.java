package com.andyadc.summer.exception;

import java.io.Serial;

public class BeanNotOfRequiredTypeException extends BeansException {

    @Serial
    private static final long serialVersionUID = -2906115359485853864L;

    public BeanNotOfRequiredTypeException() {
    }

    public BeanNotOfRequiredTypeException(String message) {
        super(message);
    }

}
