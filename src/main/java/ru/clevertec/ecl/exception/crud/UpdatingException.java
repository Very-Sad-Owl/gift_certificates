package ru.clevertec.ecl.exception.crud;

public class UpdatingException extends GeneralException {

    public UpdatingException() {
        super();
    }

    public UpdatingException(String message) {
        super(message);
    }

    public UpdatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdatingException(Throwable cause) {
        super(cause);
    }
}
