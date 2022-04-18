package ru.clevertec.ecl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
    CertificateDto certificateToDto(Certificate certificate);
    Certificate dtoToCertificate(CertificateDto dto);

    @Mapping(source = "tag", target = "tags", qualifiedByName = "tagToDtoList")
    CertificateDto filterParamsToDto(CertificateParamsDto params);
    @Mapping(source = "tag", target = "tags", qualifiedByName = "tagToList")
    Certificate filterParamsToEntity(CertificateParamsDto params);

    @Named("tagToDtoList")
    static Set<TagDto> tagToDtoList(String tag) {
        return tag == null
                ? new HashSet<>()
                : new HashSet<>(Collections.singletonList(TagDto.builder().name(tag).build()));
    }

    @Named("tagToList")
    static Set<Tag> tagToList(String tag) {
        return tag == null
                ? new HashSet<>()
                : new HashSet<>(Collections.singletonList(Tag.builder().name(tag).build()));
    }
}
