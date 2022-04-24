package ru.clevertec.ecl.exception;

public class UnsupportedFilterException extends RuntimeException{
    public UnsupportedFilterException() {
        super();
    }

    public UnsupportedFilterException(String message) {
        super(message);
    }

    public UnsupportedFilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedFilterException(Throwable cause) {
        super(cause);
    }
}
