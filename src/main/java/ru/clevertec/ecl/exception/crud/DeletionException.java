package ru.clevertec.ecl.exception.crud;

public class DeletionException extends GeneralException {

    public DeletionException() {
        super();
    }

    public DeletionException(String message) {
        super(message);
    }

    public DeletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeletionException(Throwable cause) {
        super(cause);
    }
}
