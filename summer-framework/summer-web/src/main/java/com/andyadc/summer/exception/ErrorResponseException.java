package com.andyadc.summer.exception;

import java.io.Serial;

public class ErrorResponseException extends NestedRuntimeException {

    @Serial
    private static final long serialVersionUID = -5791637311863980337L;

    public final int statusCode;

    public ErrorResponseException(int statusCode) {
        this.statusCode = statusCode;
    }

    public ErrorResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ErrorResponseException(int statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    public ErrorResponseException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

}
