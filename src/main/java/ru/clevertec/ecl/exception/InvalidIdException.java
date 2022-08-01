package ru.clevertec.ecl.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class InvalidIdException extends RuntimeException {
    private long causeId;

    public InvalidIdException(long causeId) {
        this.causeId = causeId;
    }

    public InvalidIdException(Throwable cause, long causeId) {
        super(cause);
        this.causeId = causeId;

    }
}
