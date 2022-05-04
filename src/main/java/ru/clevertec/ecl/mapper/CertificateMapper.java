package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.entity.Certificate;

@Component
@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface CertificateMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "filteringTags", ignore = true)
    CertificateDto certificateToDto(Certificate certificate);
    @InheritInverseConfiguration
    Certificate dtoToCertificate(CertificateDto dto);
}
