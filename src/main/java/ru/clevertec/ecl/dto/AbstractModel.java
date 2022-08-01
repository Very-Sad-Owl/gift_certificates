package ru.clevertec.ecl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Abstract class for all {@link ru.clevertec.ecl.dto} package's entities.
 *
 * Provides common id field for its subclasses.
 *
 * @author Olga Mailychko
 *
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractModel {
    private long id;
}
