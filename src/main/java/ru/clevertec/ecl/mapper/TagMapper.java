package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;

@Component
@Mapper(componentModel = "spring"/*, uses = {ModelMapper.class}*/)
public interface TagMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping( target = "certificates", ignore = true)
    TagDto tagToDto(Tag tag);
    @InheritInverseConfiguration
    Tag dtoToTag(TagDto dto);
}
