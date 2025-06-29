package com.andyadc.summer.exception;

import java.io.Serial;

public class NoSuchBeanDefinitionException extends BeanDefinitionException {

    @Serial
    private static final long serialVersionUID = -2226195793458711438L;

    public NoSuchBeanDefinitionException() {
    }

    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }

}
