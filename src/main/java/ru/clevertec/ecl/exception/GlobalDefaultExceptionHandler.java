package ru.clevertec.ecl.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.clevertec.ecl.interceptor.common.UrlPaths;

import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Global handler for application runtime exceptions.
 *
 * Runtime exception handled by this class are written as response entities with custom
 * {@link ErrorResponse} error response.
 *
 * See also {@link ResponseEntityExceptionHandler}.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(UrlPaths.BASE_PACKAGES_TO_SCAN)
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Source of localized error messages.
     */
    private final MessageSource messageProvider;
    /**
     * Template for detailed message of {@link NotFoundException} exception.
     */
    private static final String NOT_FOUND_ERROR_REASON_PATTERN = "(id = %s)";
    private final ObjectMapper objectMapper;

    /**
     * Handler for {@link ConstraintViolationException} exception.
     *
     * @param e handled runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> badRequestHandler(ConstraintViolationException e, Locale locale) {
        String msg = messageProvider.getMessage("error.unique_constraint_violation", null, locale);
        CustomStatusCode errorCode = CustomStatusCode.builder()
                .errorCode(CustomStatusCode.CustomCode.CONSTRAINT_VIOLATION)
                .build();
        return createResponseEntity(HttpStatus.BAD_REQUEST, msg, errorCode, e);
    }

    /**
     * Handler for {@link NotFoundException} exception.
     *
     * @param e handled runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundHandler(NotFoundException e, Locale locale) {
        String msg = messageProvider.getMessage("error.not_found", null, locale)
                + String.format(NOT_FOUND_ERROR_REASON_PATTERN, e.getCauseId());
        CustomStatusCode errorCode = CustomStatusCode.defineNotFoundCode(e.getStackTrace()[0].getClassName());
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, errorCode, e);
    }

    /**
     * Handler for {@link ServerIsDownException} exception.
     *
     * @param e handler runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(ServerIsDownException.class)
    public ResponseEntity<?> nodeDownHandler(ServerIsDownException e, Locale locale) {
        String msg = messageProvider.getMessage("error.node_is_down", null, locale);
        CustomStatusCode errorCode = CustomStatusCode.builder()
                .errorCode(CustomStatusCode.CustomCode.NODE_DOWN)
                .build();
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, msg, errorCode, e);
    }

    /**
     * Handler for {@link EmptyResultDataAccessException} exception.
     *
     * @param e handler runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> emptyResultHandler(EmptyResultDataAccessException e, Locale locale) {
        String msg = messageProvider.getMessage("error.not_found", null, locale);
        CustomStatusCode errorCode = CustomStatusCode.defineNotFoundCode(e.getStackTrace()[0].getClassName());
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, errorCode, e);
    }

    /**
     * Handler for {@link UnsupportedOperationException} exception.
     *
     * @param e handler runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<?> unsupportedOperationHandler(EmptyResultDataAccessException e, Locale locale) {
        String msg = messageProvider.getMessage("error.no_such_operation", null, locale);
        CustomStatusCode errorCode = CustomStatusCode.builder()
                .errorCode(CustomStatusCode.CustomCode.UNSUPPORTED_OPERATION)
                .build();
        return createResponseEntity(HttpStatus.BAD_REQUEST, msg, errorCode, e);
    }

    /**
     * Handler for {@link InvalidIdException} exception.
     *
     * @param e handler runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<?> unsupportedOperationHandler(InvalidIdException e, Locale locale) {
        String msg = messageProvider.getMessage("error.no_such_operation", null, locale);
        CustomStatusCode errorCode = CustomStatusCode.builder()
                .errorCode(CustomStatusCode.CustomCode.INVALID_ID)
                .build();
        return createResponseEntity(HttpStatus.BAD_REQUEST, msg, errorCode, e);
    }

    /**
     * Handler for {@link DataIntegrityViolationException} exception.
     *
     * @param e handler runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> uniqueHandler(DataIntegrityViolationException e, Locale locale) {
        String msg = messageProvider.getMessage("error.unique_constraint_violation", null, locale);
        CustomStatusCode errorCode = CustomStatusCode.builder()
                .errorCode(CustomStatusCode.CustomCode.DATA_INTEGRITY_VIOLATION)
                .build();
        return createResponseEntity(HttpStatus.BAD_REQUEST, msg, errorCode, e);
    }

    /**
     * Handler for {@link HttpClientErrorException} exception.
     *
     * @param e handler runtime exception
     * @param locale current locale defined by user request's header
     * @return {@link ResponseEntity} including HTTP status code and {@link ErrorResponse} object as body
     */
    @SneakyThrows
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleXXException(HttpClientErrorException e, Locale locale) {
        String substring = e.getMessage().substring(7, e.getMessage().length() - 1);
        ErrorResponse errorResponse = objectMapper.readValue(substring, ErrorResponse.class);
        return ResponseEntity
                .status(e.getStatusCode())
                .body(errorResponse);
    }

    /**
     * This method builds custom error response for user.
     *
     * @param status HTTP status of response
     * @param message detailed error message
     * @param errorCode custom error code defined in {@link CustomStatusCode}
     * @param e occurred exception
     * @return {@link ResponseEntity} including HTTP status code and error message as body
     */
    private ResponseEntity<?> createResponseEntity(HttpStatus status, String message, CustomStatusCode errorCode, Exception e) {
        log.warn(String.format(e.getClass().getTypeName(), e.getMessage()));
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .timeOccurred(Timestamp.valueOf(LocalDateTime.now()))
                        .status(status.value())
                        .errorName(status.getReasonPhrase())
                        .errorMsg(message)
                        .excName(e.getClass().getName())
                        .errorCode(errorCode.getIntCode())
                        .build()
                );
    }

}
