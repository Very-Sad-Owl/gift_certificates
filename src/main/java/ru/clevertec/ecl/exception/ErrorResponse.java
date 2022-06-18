package ru.clevertec.ecl.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Builder
@Getter
public class ErrorResponse {
    private Timestamp timeOccurred;
    private int status;
    private String errorName;
    private String errorMsg;
    private String excName;

}
