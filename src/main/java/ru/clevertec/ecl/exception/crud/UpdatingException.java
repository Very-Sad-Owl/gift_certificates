package ru.clevertec.ecl.exception.crud;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdatingException extends CrudException {
    public UpdatingException(long causeId) {
        super(causeId);
    }

    public UpdatingException(Throwable cause, long causeId) {
        super(cause, causeId);
    }
}
