package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DynamicUpdate
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
public class User extends AbstractEntity {

    @Column(nullable = false)
    private String login;
    @Column(nullable = false)
    private String surname;
//    @Column(nullable = false)
//    private String encryptedPassword;
//    @Column
//    @Enumerated(EnumType.STRING)
//    private Role role;

}
