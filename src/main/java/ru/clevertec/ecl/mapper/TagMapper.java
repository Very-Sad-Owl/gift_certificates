package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Tag;

@Component
@Mapper(componentModel = "spring")
public interface TagMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TagDto tagToDto(Tag tag);
    @InheritInverseConfiguration
    Tag dtoToTag(TagDto dto);
}
