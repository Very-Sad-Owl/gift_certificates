package ru.clevertec.ecl.exception;

import ru.clevertec.ecl.exception.crud.GeneralException;

public class UndefinedException extends GeneralException {

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
