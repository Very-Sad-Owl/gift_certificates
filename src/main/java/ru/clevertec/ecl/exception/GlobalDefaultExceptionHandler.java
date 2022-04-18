package ru.clevertec.ecl.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.clevertec.ecl.exception.crud.notfound.NotFoundException;
import ru.clevertec.ecl.util.localization.MessageProvider;
import ru.clevertec.ecl.exception.crud.*;
import ru.clevertec.ecl.util.Constant;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Slf4j
@ControllerAdvice(Constant.BASE_PACKAGES_TO_SCAN)
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageProvider messageProvider;
    private static final String ERROR_REASON_PATTERN = "(id = %s)";

    @Autowired
    public GlobalDefaultExceptionHandler(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @ExceptionHandler(Exception.class)
    public void handlerGlobal(Exception e, HttpServletResponse response, PrintWriter writer) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        writer.print(messageProvider.getMessage(UndefinedException.class.getSimpleName())
                + e.getMessage());
    }

    @ExceptionHandler({NoRequiredArgsException.class, InvalidArgException.class})
    public ResponseEntity<?> badRequestHandler(GeneralException e) {
        return createResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({DeletionException.class, UpdatingException.class, SavingException.class})
    public ResponseEntity<?> postHandler(GeneralException e) {
        if (e.getCause() instanceof NotFoundException) {
            String msg = messageProvider.getMessage(e.getClass().getSimpleName())
                    + messageProvider.getMessage(e.getCause().getClass().getSimpleName())
                    + String.format(ERROR_REASON_PATTERN, e.getCause().getMessage());
            return createResponseEntity(HttpStatus.NOT_FOUND, msg, e);
        } else {
            return createResponseEntity(HttpStatus.NOT_FOUND, e);
        }
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<?> notFoundHandler(NotFoundException e) {
        String msg = messageProvider.getMessage(e.getClass().getSimpleName())
                + String.format(ERROR_REASON_PATTERN, e.getMessage());
        return createResponseEntity(HttpStatus.NOT_FOUND, msg, new NotFoundException(e));
    }

    private ResponseEntity<?> createResponseEntity(HttpStatus status, Exception e) {
        log.error(String.format(e.getClass().getTypeName(), e.getMessage()));
        return ResponseEntity.status(status.value())
                .body(ErrorResponse.builder()
                        .timeOccurred(Timestamp.valueOf(LocalDateTime.now()))
                        .status(status.value())
                        .errorName(status.getReasonPhrase())
                        .errorMsg(messageProvider.getMessage(e.getClass().getSimpleName()))
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
