package ru.clevertec.ecl.entity.baseentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.*;

/**
 * Entity class for order abstraction.
 *
 * See also {@link javax.persistence.Entity}.
 *
 * @author Olga Mailychko
 *
 */
@Entity
@DynamicUpdate
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table(name = "orders")
public class Order extends AbstractEntity{

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = ORDERS_SEQUENCE
    )
    @SequenceGenerator(
            name = ORDERS_SEQUENCE,
            sequenceName = ORDERS_SEQUENCE,
            allocationSize = 1,
            initialValue = 1
    )
    private long id;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private double price;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private long certificateId;

    @NotNull
    @Column(nullable = false)
    @Positive
    private long userId;

}