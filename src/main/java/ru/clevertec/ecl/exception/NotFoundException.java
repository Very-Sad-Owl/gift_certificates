package ru.clevertec.ecl.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotFoundException extends RuntimeException {
    private long causeId;

    public NotFoundException(long causeId) {
        this.causeId = causeId;
    }

    public NotFoundException(Throwable cause, long causeId) {
        super(cause);
        this.causeId = causeId;

    }
}
