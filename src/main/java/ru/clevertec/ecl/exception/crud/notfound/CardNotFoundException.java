package ru.clevertec.ecl.exception.crud.notfound;

public class CardNotFoundException extends NotFoundException {

    public CardNotFoundException() {
        super();
    }

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardNotFoundException(Throwable cause) {
        super(cause);
    }

}
