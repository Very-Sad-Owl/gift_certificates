package ru.clevertec.ecl.exception.crud;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CrudException extends GeneralException {

    protected long causeId;

    public CrudException(long causeId) {
        this.causeId = causeId;
    }

    public CrudException(Throwable cause, long causeId) {
        super(cause);
        this.causeId = causeId;
    }
}
