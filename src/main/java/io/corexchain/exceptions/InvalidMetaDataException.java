package io.corexchain.exceptions;

public class InvalidMetaDataException extends RuntimeException {
    public InvalidMetaDataException(){

    }

    public InvalidMetaDataException(String message) {
        super(message);
    }

    public InvalidMetaDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMetaDataException(Throwable cause) {
        super(cause);
    }

    protected InvalidMetaDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
