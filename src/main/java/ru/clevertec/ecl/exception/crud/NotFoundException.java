package ru.clevertec.ecl.exception.crud;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.clevertec.ecl.exception.crud.GeneralException;

@NoArgsConstructor
@Getter
public class NotFoundException extends CrudException {
    public NotFoundException(long causeId) {
        super(causeId);
    }

    public NotFoundException(Throwable cause, long causeId) {
        super(cause, causeId);
    }
}
