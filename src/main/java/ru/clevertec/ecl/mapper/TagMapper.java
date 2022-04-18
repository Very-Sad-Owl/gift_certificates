package ru.clevertec.ecl.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;

@Component
@Mapper(componentModel = "spring"/*, uses = {ModelMapper.class}*/)
public interface TagMapper {
    @Mapping( target = "certificates", ignore = true)
    TagDto tagToDto(Tag tag);
    @InheritInverseConfiguration
    Tag dtoToTag(TagDto dto);
}
