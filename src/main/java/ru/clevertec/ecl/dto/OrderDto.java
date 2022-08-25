package ru.clevertec.ecl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO class for transfer {@link ru.clevertec.ecl.entity.baseentities.Order} data.
 *
 * Transfers entity's field data between controller and repository layers.
 *
 * @author Olga Mailychko
 *
 */
@Data
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class OrderDto extends AbstractModel {
    private double price;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purchaseTime;
    private CertificateDto certificate;
    private UserDto user;
    private long userId;
    private long certificateId;

    public void calculatePrice() {
        this.price = certificate.getPrice();
    }

}