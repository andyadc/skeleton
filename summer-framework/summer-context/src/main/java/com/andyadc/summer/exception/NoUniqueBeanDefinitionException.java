package com.andyadc.summer.exception;

import java.io.Serial;

public class NoUniqueBeanDefinitionException extends BeanDefinitionException {

    @Serial
    private static final long serialVersionUID = -8706936007393229694L;

    public NoUniqueBeanDefinitionException() {
    }

    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }

}
