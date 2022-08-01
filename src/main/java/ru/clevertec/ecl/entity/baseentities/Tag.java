package ru.clevertec.ecl.entity.baseentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.*;

/**
 * Entity class for tag abstraction.
 *
 * See also {@link javax.persistence.Entity}.
 *
 * @author Olga Mailychko
 *
 */
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DynamicUpdate
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class Tag extends AbstractEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = TAGS_SEQUENCE
    )
    @SequenceGenerator(
            name = TAGS_SEQUENCE,
            sequenceName = TAGS_SEQUENCE,
            allocationSize = 1,
            initialValue = 1
    )
    private long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;
}
