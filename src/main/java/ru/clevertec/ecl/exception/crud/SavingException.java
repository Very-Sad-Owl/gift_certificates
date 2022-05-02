package ru.clevertec.ecl.exception.crud;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SavingException extends CrudException {
    public SavingException(long causeId) {
        super(causeId);
    }

    public SavingException(Throwable cause, long causeId) {
        super(cause, causeId);
    }
}
