package io.corexchain.verify4j.exceptions;

public class InvalidSmartContractException extends RuntimeException {
    public InvalidSmartContractException(){

    }

    public InvalidSmartContractException(String message) {
        super(message);
    }

    public InvalidSmartContractException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSmartContractException(Throwable cause) {
        super(cause);
    }

    protected InvalidSmartContractException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
