package ru.clevertec.ecl.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
public class Tag extends AbstractEntity {

    @ToString.Include
    @Column(unique = true, nullable = false)
    private String name;

    @Builder
    public Tag(long id, String name, Set<Certificate> certificates) {
        super(id);
        this.name = name;
        this.certificates = certificates;
    }

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    private Set<Certificate> certificates = new LinkedHashSet<>();
}
