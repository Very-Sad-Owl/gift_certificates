package ru.clevertec.ecl.exception.crud.notfound;

public class NoContentException extends NotFoundException {

    public NoContentException() {
        super();
    }

    public NoContentException(String message) {
        super(message);
    }

    public NoContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoContentException(Throwable cause) {
        super(cause);
    }

}
