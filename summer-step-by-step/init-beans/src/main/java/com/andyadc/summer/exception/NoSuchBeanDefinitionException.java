package com.andyadc.summer.exception;

public class NoSuchBeanDefinitionException extends BeanDefinitionException {

    private static final long serialVersionUID = -2226195793458711438L;

    public NoSuchBeanDefinitionException() {
    }

    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }

}
