package ru.clevertec.ecl.exception.crud.notfound;

public class ProductDiscountNotFoundException extends NotFoundException {

    public ProductDiscountNotFoundException() {
        super();
    }

    public ProductDiscountNotFoundException(String message) {
        super(message);
    }

    public ProductDiscountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductDiscountNotFoundException(Throwable cause) {
        super(cause);
    }

}
