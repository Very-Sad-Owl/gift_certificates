package ru.clevertec.ecl.exception;

public class UndefinedException extends RuntimeException {

    public UndefinedException(String message) {
        super(message);
    }

    public UndefinedException() {
        super();
    }

    public UndefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedException(Throwable cause) {
        super(cause);
    }

    protected UndefinedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
