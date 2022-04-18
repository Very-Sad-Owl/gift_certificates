package ru.clevertec.ecl.exception.crud.notfound;

public class CashierNotFoundException extends NotFoundException {

    public CashierNotFoundException() {
        super();
    }

    public CashierNotFoundException(String message) {
        super(message);
    }

    public CashierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CashierNotFoundException(Throwable cause) {
        super(cause);
    }

}
