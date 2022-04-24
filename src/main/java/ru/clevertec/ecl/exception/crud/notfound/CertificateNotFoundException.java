package ru.clevertec.ecl.exception.crud.notfound;

public class CertificateNotFoundException extends NotFoundException {

    public CertificateNotFoundException() {
        super();
    }

    public CertificateNotFoundException(String message) {
        super(message);
    }

    public CertificateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateNotFoundException(Throwable cause) {
        super(cause);
    }

}
