package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DynamicUpdate
@ToString(callSuper = true)
@EqualsAndHashCode(exclude = "certificates", callSuper = false)
public class Tag extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name;
}
