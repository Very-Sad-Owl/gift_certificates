package ru.clevertec.ecl.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Builder
@Getter
public class ErrorResponse {
    public Timestamp timeOccurred;
    public int status;
    public String errorName;
    public String errorMsg;
    public String excName;

}
