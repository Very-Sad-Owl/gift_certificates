package ru.clevertec.ecl.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ServerIsDownException extends RuntimeException {
    private long causeId;

    public ServerIsDownException(long causeId) {
        this.causeId = causeId;
    }

    public ServerIsDownException(Throwable cause, long causeId) {
        super(cause);
        this.causeId = causeId;

    }
}
