package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
@EqualsAndHashCode(callSuper = true)
public class Tag extends AbstractEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_tag"
    )
    @SequenceGenerator(
            name = "seq_tag",
            sequenceName="seq_tag",
            allocationSize = 1,
            initialValue = 1
    )
    private long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;
}
