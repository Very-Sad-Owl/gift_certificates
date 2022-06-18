package ru.clevertec.ecl.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@DynamicUpdate
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "gift_certificate")
public class Certificate extends AbstractEntity{

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_cert"
    )
    @SequenceGenerator(
            name = "seq_cert",
            sequenceName="seq_cert",
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
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "certificate_tag",
            joinColumns = @JoinColumn(name = "certificate_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new LinkedHashSet<>();

}