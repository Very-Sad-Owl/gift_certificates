package ru.clevertec.ecl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import ru.clevertec.ecl.entity.AbstractEntity;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class OrderDto extends AbstractModel {

    private double price;
    private LocalDateTime purchaseTime;
    private CertificateDto certificate;
    private UserDto user;
    @JsonIgnore
    private long userId;
    @JsonIgnore
    private long certificateId;

    public void calculatePrice() {
        this.price = certificate.getPrice();
    }

}