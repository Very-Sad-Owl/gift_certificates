package ru.clevertec.ecl.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DTO class for transfer {@link ru.clevertec.ecl.entity.baseentities.User} data.
 *
 * Transfers entity's field data between controller and repository layers.
 *
 * @author Olga Mailychko
 *
 */
@Data
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDto extends AbstractModel {
    private String name;
    private String surname;

}
