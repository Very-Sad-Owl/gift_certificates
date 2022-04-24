package ru.clevertec.ecl.dto;

import lombok.*;
import ru.clevertec.ecl.entity.Tag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"lastUpdateDate", "createDate", "filteringTag"})
@ToString(callSuper = true)
public class CertificateDto extends AbstractModel {
    private String name;
    private String description;
    private double price;
    private Integer duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private Set<TagDto> tags;

    private String filteringTag;

    @Builder
    public CertificateDto(long id, String name, String description, double price, Integer duration, LocalDateTime createDate, LocalDateTime lastUpdateDate, Set<TagDto> tags, String filteringTag) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
        this.filteringTag = filteringTag;
    }
}