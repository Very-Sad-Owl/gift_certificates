package ru.clevertec.ecl.exception.crud;

import lombok.NoArgsConstructor;
import ru.clevertec.ecl.exception.crud.NotFoundException;
@NoArgsConstructor
public class NoContentException extends CrudException {
    public NoContentException(long causeId) {
        super(causeId);
    }
    public NoContentException(Throwable cause, long causeId) {
        super(cause, causeId);
    }
}
