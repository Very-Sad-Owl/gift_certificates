package ru.clevertec.ecl.repository.exception;

public class ReadingException extends RepositoryException {
    public ReadingException() {
        super();
    }

    public ReadingException(String message) {
        super(message);
    }

    public ReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadingException(Throwable cause) {
        super(cause);
    }

    protected ReadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
