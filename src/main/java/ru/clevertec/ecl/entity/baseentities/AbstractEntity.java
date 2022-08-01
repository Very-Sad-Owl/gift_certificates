package ru.clevertec.ecl.entity.baseentities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * Abstract class for all {@link ru.clevertec.ecl.entity.baseentities} package's entities.
 *
 * Declares common getter method for id implemented by its subclasses.
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
public abstract long getId();

}
