package ru.clevertec.ecl.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
class TagMapperTest {

    @Autowired
    private TagMapper mapper;

    @Configuration
    public static class Config {

        @Bean
        public TagMapper converterUsingTagMapper() {
            return Mappers.getMapper(TagMapper.class);
        }
    }

    @Test
    void tagToDto_equalEntities_equalDtos() {
        Tag source = Tag.builder()
                .id(1)
                .name("tag")
//                .certificates(new HashSet<>())
                .build();

        TagDto expected = TagDto.builder()
                .id(1)
                .name("tag")
                .build();

        TagDto actual = mapper.tagToDto(source);

        assertEquals(expected, actual);
    }

    @Test
    void dtoToTag_equalDtos_equalEntities() {
        TagDto source;
        source = TagDto.builder()
                .id(0)
                .name("tag")
//                .certificates(new HashSet<>())
                .build();

        Tag expected = Tag.builder()
                .id(0)
                .name("tag")
                .build();

        Tag actual = mapper.dtoToTag(source);

        assertEquals(expected, actual);
    }
}