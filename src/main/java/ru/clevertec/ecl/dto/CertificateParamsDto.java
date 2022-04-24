package ru.clevertec.ecl.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CertificateParamsDto extends AbstractModel{
    String name;
    String description;
    String tagName;
}
