package com.andyadc.summer.exception;

public class BeanDefinitionException extends BeansException {

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
