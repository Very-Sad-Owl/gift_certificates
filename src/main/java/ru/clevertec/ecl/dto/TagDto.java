package ru.clevertec.ecl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.clevertec.ecl.entity.Certificate;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TagDto extends AbstractModel {
    private String name;
//    @JsonIgnore
//    private Set<CertificateDto> certificates;

//    @Builder
//    public TagDto(long id, String name, Set<CertificateDto> certificates) {
//        super(id);
//        this.name = name;
//        this.certificates = certificates;
//    }
}
