package com.andyadc.summer.exception;

public class NoUniqueBeanDefinitionException extends BeanDefinitionException {

    private static final long serialVersionUID = -8706936007393229694L;

    public NoUniqueBeanDefinitionException() {
    }

    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }

}
