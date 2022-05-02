package io.corexchain.exceptions;

public class InvalidCreditAmountException extends RuntimeException {
    public InvalidCreditAmountException(){

    }

    public InvalidCreditAmountException(String message) {
        super(message);
    }

    public InvalidCreditAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCreditAmountException(Throwable cause) {
        super(cause);
    }

    protected InvalidCreditAmountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
