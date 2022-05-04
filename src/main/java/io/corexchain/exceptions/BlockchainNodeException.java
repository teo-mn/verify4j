package io.corexchain.exceptions;

public class BlockchainNodeException extends RuntimeException {
    public BlockchainNodeException() {

    }

    public BlockchainNodeException(String message) {
        super(message);
    }

    public BlockchainNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockchainNodeException(Throwable cause) {
        super(cause);
    }

    protected BlockchainNodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
