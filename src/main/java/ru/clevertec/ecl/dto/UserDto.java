package ru.clevertec.ecl.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import ru.clevertec.ecl.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDto extends AbstractModel {
    private String name;
    private String surname;
    private String password;
    private String role;

}
