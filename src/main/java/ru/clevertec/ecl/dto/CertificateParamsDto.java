package ru.clevertec.ecl.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CertificateParamsDto extends AbstractModel{
    String name;
    String description;
    List<String> tags;
}
