package io.corexchain.verify4j.exceptions;

public class InvalidChainPointException extends RuntimeException {
    public InvalidChainPointException() {

    }

    public InvalidChainPointException(String message) {
        super(message);
    }

    public InvalidChainPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidChainPointException(Throwable cause) {
        super(cause);
    }

    protected InvalidChainPointException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
