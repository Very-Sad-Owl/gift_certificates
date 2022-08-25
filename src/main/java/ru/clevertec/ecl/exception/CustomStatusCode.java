package ru.clevertec.ecl.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides custom status codes for exceptions.
 * <p>
 * Codes are based on corresponding HTTP status codes and ordinal number of certain exception.
 * <p>
 * See also {@link org.springframework.http.HttpStatus}.
 *
 * @author Olga Mailychko
 */
@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CustomStatusCode {

    /**
     * Constant of {@link CustomCode} enum defining error type
     */
    private CustomCode errorCode;

    /**
     * @return int value of certain error
     */
    public int getIntCode() {
        return errorCode.code;
    }

    /**
     * Defines concrete type of {@link NotFoundException} by service class where exception has been thrown
     *
     * @param classNameThrownFrom name of class where exception has been thrown
     * @return {@link CustomStatusCode} object containing certain {@link CustomCode} constant
     */
    public static CustomStatusCode defineNotFoundCode(String classNameThrownFrom) {
        if (classNameThrownFrom.contains(TagService.class.getName())) {
            return CustomStatusCode.builder()
                    .errorCode(CustomCode.TAG_NOT_FOUND)
                    .build();
        } else if (classNameThrownFrom.contains(CertificateService.class.getName())) {
            return CustomStatusCode.builder()
                    .errorCode(CustomCode.CERTIFICATE_NOT_FOUND)
                    .build();
        } else if (classNameThrownFrom.contains(OrderService.class.getName())) {
            return CustomStatusCode.builder()
                    .errorCode(CustomCode.ORDER_NOT_FOUND)
                    .build();
        } else if (classNameThrownFrom.contains(UserService.class.getName())) {
            return CustomStatusCode.builder()
                    .errorCode(CustomCode.USER_NOT_FOUND)
                    .build();
        } else {
            return CustomStatusCode.builder()
                    .errorCode(CustomCode.NOT_FOUND)
                    .build();
        }
    }

    /**
     * Enum storing constants corresponding certain exceptions with its custom codes.
     *
     * @author Olga Mailychko
     */
    @AllArgsConstructor
    @Getter
    public enum CustomCode {
        TAG_NOT_FOUND(40401), CERTIFICATE_NOT_FOUND(40402), ORDER_NOT_FOUND(40403), USER_NOT_FOUND(40404), NOT_FOUND(40400),
        CONSTRAINT_VIOLATION(40001), BAD_CONSTRAINT(40002), DATA_INTEGRITY_VIOLATION(40003), UNSUPPORTED_OPERATION(40004),
        INVALID_ID(40005), BAD_REQUEST(40000),
        NODE_DOWN(50001);

        private final int code;
    }
}
