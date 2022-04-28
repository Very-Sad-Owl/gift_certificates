package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
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
@EqualsAndHashCode(callSuper = false)
@Table(name = "orders")
public class Order extends AbstractEntity{

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    @OneToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}