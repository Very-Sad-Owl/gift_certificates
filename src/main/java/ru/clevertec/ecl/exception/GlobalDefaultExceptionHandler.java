package ru.clevertec.ecl.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.clevertec.ecl.exception.crud.NotFoundException;
import ru.clevertec.ecl.exception.crud.*;
import ru.clevertec.ecl.util.Constant;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;


@Slf4j
@RestControllerAdvice(Constant.BASE_PACKAGES_TO_SCAN)
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageProvider;
    private static final String ERROR_REASON_PATTERN = "(id = %s)";

    @Autowired
    public GlobalDefaultExceptionHandler(MessageSource messageProvider) {
        this.messageProvider = messageProvider;
    }

    @ExceptionHandler(UndefinedException.class)
    public ResponseEntity<?> handlerGlobal(UndefinedException e, Locale locale) {
        String msg = messageProvider.getMessage("error.undefined", null, locale);
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, msg, e);
    }

    @ExceptionHandler({NoRequiredArgsException.class, InvalidArgException.class})
    public ResponseEntity<?> badRequestHandler(GeneralException e) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(DeletionException.class)
    public ResponseEntity<?> deleteHandler(DeletionException e, Locale locale) {
        String msg = messageProvider.getMessage("error.delete", null, locale);
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, e);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundHandler(NotFoundException e, Locale locale) {
        String msg = messageProvider.getMessage("error.not_found", null, locale)
                + String.format(ERROR_REASON_PATTERN, e.getCauseId());
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, e);
    }


    @ExceptionHandler(UpdatingException.class)
    public ResponseEntity<?> updateHandler(UpdatingException e, Locale locale) {
        String msg = messageProvider.getMessage("error.update", null, locale)
                + String.format(ERROR_REASON_PATTERN, e.getCauseId());
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, e);
    }

    @ExceptionHandler(SavingException.class)
    public ResponseEntity<?> savingHandler(SavingException e, Locale locale) {
        String msg = messageProvider.getMessage("error.save", null, locale)
                + String.format(ERROR_REASON_PATTERN, e.getCauseId());
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, e);
    }

    private ResponseEntity<?> createResponseEntity(HttpStatus status, Exception e) {
        log.error(String.format(e.getClass().getTypeName(), e.getMessage()));
        return ResponseEntity.status(status.value())
                .body(ErrorResponse.builder()
                        .timeOccurred(Timestamp.valueOf(LocalDateTime.now()))
                        .status(status.value())
                        .errorName(status.getReasonPhrase())
                        .errorMsg(e.getMessage())
                        .excName(e.getClass().getName())
                        .build());
    }

    private ResponseEntity<?> createResponseEntity(HttpStatus status, String message, Exception e) {
        log.error(String.format(e.getClass().getTypeName(), e.getMessage()));
        return ResponseEntity.status(status.value())
                .body(ErrorResponse.builder()
                        .timeOccurred(Timestamp.valueOf(LocalDateTime.now()))
                        .status(status.value())
                        .errorName(status.getReasonPhrase())
                        .errorMsg(message)
                        .excName(e.getClass().getName())
                        .build());
    }

}
