package ru.clevertec.ecl.exception.crud;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeletionException extends CrudException {
    public DeletionException(long causeId) {
        super(causeId);
    }

    public DeletionException(Throwable cause, long causeId) {
        super(cause, causeId);
    }
}
