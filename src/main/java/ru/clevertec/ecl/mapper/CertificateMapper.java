package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.CertificateParamsDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.Tag;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface CertificateMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CertificateDto certificateToDto(Certificate certificate);
    @InheritInverseConfiguration
    Certificate dtoToCertificate(CertificateDto dto);
}
