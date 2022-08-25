package ru.clevertec.ecl.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO class for transfer {@link ru.clevertec.ecl.entity.baseentities.Tag} data.
 *
 * Transfers entity's field data between controller and repository layers.
 *
 * @author Olga Mailychko
 *
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TagDto extends AbstractModel {
    private String name;
}
