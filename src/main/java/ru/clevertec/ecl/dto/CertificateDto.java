package ru.clevertec.ecl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.clevertec.ecl.entity.Tag;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"lastUpdateDate", "createDate", "filteringTags"})
@ToString(callSuper = true)
public class CertificateDto extends AbstractModel {
    private String name;
    private String description;
    private double price;
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private Set<TagDto> tags;
    @JsonIgnore
    private Set<String> filteringTags;
}