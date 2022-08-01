package ru.clevertec.ecl.entity.baseentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.CERTIFICATES_SEQUENCE;

/**
 * Entity class for certificate abstraction.
 *
 * See also {@link javax.persistence.Entity}.
 *
 * @author Olga Mailychko
 *
 */
@Entity
@DynamicUpdate
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = false)
@Table(name = "gift_certificate")
public class Certificate extends AbstractEntity{

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = CERTIFICATES_SEQUENCE
    )
    @SequenceGenerator(
            name = CERTIFICATES_SEQUENCE,
            sequenceName= CERTIFICATES_SEQUENCE,
            allocationSize = 1,
            initialValue = 1
    )
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    private String description;

    @Positive
    @Digits(integer = 4, fraction = 2)
    @NotNull
    @Column(nullable = false)
    private double price;

    @Min(1)
    @NotNull
    @Column(nullable = false)
    private Integer duration;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime lastUpdateDate;

    @NotNull
    @ToString.Exclude
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "certificate_tag",
            joinColumns = @JoinColumn(name = "certificate_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

}