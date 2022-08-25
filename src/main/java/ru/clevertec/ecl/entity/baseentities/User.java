package ru.clevertec.ecl.entity.baseentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.USERS_SEQUENCE;

/**
 * Entity class for user abstraction.
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Table(name = "users")
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = USERS_SEQUENCE
    )
    @SequenceGenerator(
            name = USERS_SEQUENCE,
            sequenceName = USERS_SEQUENCE,
            allocationSize = 1,
            initialValue = 1
    )
    private long id;

    @NotBlank
    @Length(min = 2, max = 16)
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Length(min = 2, max = 16)
    private String surname;

}
