package com.andyadc.summer.exception;

public class UnsatisfiedDependencyException extends BeanCreationException {

    private static final long serialVersionUID = 5124224373066485292L;

    public UnsatisfiedDependencyException() {
    }

    public UnsatisfiedDependencyException(String message) {
        super(message);
    }

    public UnsatisfiedDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsatisfiedDependencyException(Throwable cause) {
        super(cause);
    }

}
