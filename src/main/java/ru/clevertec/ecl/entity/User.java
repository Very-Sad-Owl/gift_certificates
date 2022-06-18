package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_user"
    )
    @SequenceGenerator(
            name = "seq_user",
            sequenceName="seq_user",
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
//    @Column(nullable = false)
//    private String encryptedPassword;
//    @Column
//    @Enumerated(EnumType.STRING)
//    private Role role;

}
