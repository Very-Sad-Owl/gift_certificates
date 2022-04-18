package ru.clevertec.ecl.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagMapperTest {

    @Autowired
    private TagMapper mapper;

    @Test
    void tagToDto() {
        Tag source;
        source = Tag.builder()
                .id(1)
                .name("tag")
                .certificates(new HashSet<>())
                .build();
        TagDto dto = mapper.tagToDto(source);
        assertNotNull(dto);
    }

    @Test
    void dtoToTag() {
        TagDto source;
        source = TagDto.builder()
                .id(0)
                .name("tag")
                .build();
        Tag tag = mapper.dtoToTag(source);
        assertNotNull(tag);
    }
}