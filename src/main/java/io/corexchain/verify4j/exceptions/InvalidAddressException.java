package io.corexchain.verify4j.exceptions;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(){

    }

    public InvalidAddressException(String message) {
        super(message);
    }

    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAddressException(Throwable cause) {
        super(cause);
    }

    protected InvalidAddressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
