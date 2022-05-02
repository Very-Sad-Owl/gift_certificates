package ru.clevertec.ecl.exception;

import ru.clevertec.ecl.exception.crud.GeneralException;

public class NoRequiredArgsException extends GeneralException {
    public NoRequiredArgsException() {
        super();
    }

    public NoRequiredArgsException(String message) {
        super(message);
    }

    public NoRequiredArgsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoRequiredArgsException(Throwable cause) {
        super(cause);
    }

    protected NoRequiredArgsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
