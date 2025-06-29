package com.andyadc.summer.exception;

import java.io.Serial;

public class BeanDefinitionException extends BeansException {

    @Serial
    private static final long serialVersionUID = -6903811012406980655L;

    public BeanDefinitionException() {
    }

    public BeanDefinitionException(String message) {
        super(message);
    }

    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanDefinitionException(Throwable cause) {
        super(cause);
    }

}
