package ru.clevertec.ecl.entity;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DynamicUpdate
@ToString(callSuper = true)
@EqualsAndHashCode(exclude = "certificates", callSuper = false)
public class Tag extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name;
//
//    @Builder
//    public Tag(long id, String name, Set<Certificate> certificates) {
//        super(id);
//        this.name = name;
//        this.certificates = certificates;
//    }

    @Builder
    public Tag(long id, String name) {
        super(id);
        this.name = name;
    }

//    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
//    @ManyToMany(mappedBy = "tags"/*, cascade = {CascadeType.PERSIST, CascadeType.MERGE}*/)
//    @ManyToMany(mappedBy = "tags")
//    private Set<Certificate> certificates = new LinkedHashSet<>();
}
