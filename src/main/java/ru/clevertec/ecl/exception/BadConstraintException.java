package ru.clevertec.ecl.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BadConstraintException extends RuntimeException {
    private long causeId;

    public BadConstraintException(long causeId) {
        this.causeId = causeId;
    }

    public BadConstraintException(Throwable cause, long causeId) {
        super(cause);
        this.causeId = causeId;

    }
}
