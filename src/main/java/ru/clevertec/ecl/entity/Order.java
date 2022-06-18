package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@DynamicUpdate
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "orders")
public class Order extends AbstractEntity{

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq"
    )
    @SequenceGenerator(
            name = "seq",
            sequenceName="seq",
            allocationSize = 1,
            initialValue = 1
    )
    private long id;

    @NotNull
    @Positive
//    @Digits(integer = 4, fraction = 2)
    @Column(nullable = false)
    private double price;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    @NotNull
    @Positive
    private long certificateId;

    @NotNull
    @Positive
    private long userId;

}