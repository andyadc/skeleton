package com.andyadc.summer.exception;

import java.io.Serial;

public class ServerWebInputException extends ErrorResponseException {

    @Serial
    private static final long serialVersionUID = 5244547673230625900L;

    public ServerWebInputException() {
        super(400);
    }

    public ServerWebInputException(String message) {
        super(400, message);
    }

    public ServerWebInputException(Throwable cause) {
        super(400, cause);
    }

    public ServerWebInputException(String message, Throwable cause) {
        super(400, message, cause);
    }

}
