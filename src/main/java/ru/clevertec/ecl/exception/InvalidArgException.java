package ru.clevertec.ecl.exception;

import ru.clevertec.ecl.exception.crud.GeneralException;

public class InvalidArgException extends GeneralException {

    public InvalidArgException(String message) {
        super(message);
    }

    public InvalidArgException() {
        super();
    }

    public InvalidArgException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
