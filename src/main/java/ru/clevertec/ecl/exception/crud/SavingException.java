package ru.clevertec.ecl.exception.crud;

public class SavingException extends GeneralException {

    public SavingException() {
        super();
    }

    public SavingException(String message) {
        super(message);
    }

    public SavingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SavingException(Throwable cause) {
        super(cause);
    }
}
