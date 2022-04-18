package ru.clevertec.ecl.exception.crud.notfound;

public class BillNotFoundException extends NotFoundException {

    public BillNotFoundException() {
        super();
    }

    public BillNotFoundException(String message) {
        super(message);
    }

    public BillNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BillNotFoundException(Throwable cause) {
        super(cause);
    }

}
