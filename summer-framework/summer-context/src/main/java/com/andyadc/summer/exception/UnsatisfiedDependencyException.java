package com.andyadc.summer.exception;

import java.io.Serial;

public class UnsatisfiedDependencyException extends BeanCreationException {

    @Serial
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
