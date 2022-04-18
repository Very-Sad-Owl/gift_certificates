package ru.clevertec.ecl.exception.crud.notfound;

public class CardTypeNotFoundException extends NotFoundException {

    public CardTypeNotFoundException() {
        super();
    }

    public CardTypeNotFoundException(String message) {
        super(message);
    }

    public CardTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardTypeNotFoundException(Throwable cause) {
        super(cause);
    }

}
