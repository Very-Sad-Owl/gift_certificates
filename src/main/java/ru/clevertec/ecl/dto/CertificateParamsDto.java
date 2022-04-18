package ru.clevertec.ecl.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CertificateParamsDto {
    String name;
    String description;
    String tag;
}
