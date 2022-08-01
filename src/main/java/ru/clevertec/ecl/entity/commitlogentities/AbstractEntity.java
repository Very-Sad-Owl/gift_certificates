package ru.clevertec.ecl.entity.commitlogentities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Abstract class for all {@link ru.clevertec.ecl.entity.commitlogentities} package's entities.
 *
 * Declares common id field for all its subclasses.
 *
 * See also {@link javax.persistence.MappedSuperclass}.
 *
 * @author Olga Mailychko
 *
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
