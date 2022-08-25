package ru.clevertec.ecl.exception;

import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;

/**
 * Class containing fields of custom error response for user.
 *
 * See also {@link org.springframework.http.HttpStatus}.
 *
 * @author Olga Mailychko
 *
 */
@Builder
@Getter
public class ErrorResponse {
    private Timestamp timeOccurred;
    private int status;
    private String errorName;
    private String errorMsg;
    private String excName;
    private int errorCode;

}
