package ru.clevertec.ecl.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.Tag;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
class CertificateMapperTest {

    @Autowired
    CertificateMapper mapper;

    @Test
    void certificateToDto_equalEntities_equalDtos() {
        Tag exTag = Tag.builder()
                .id(1)
                .name("tag")
                .build();
        TagDto exTagDto = TagDto.builder()
                .id(1)
                .name("tag")
                .build();
        Set<Tag> tags = new HashSet<>();
        Set<TagDto> tagDtos = new HashSet<>();
        tags.add(exTag);
        tagDtos.add(exTagDto);
        LocalDateTime creationAndUpdate = LocalDateTime.now();

        Certificate source = Certificate.builder()
                .id(1)
                .name("name")
                .description("xd")
                .createDate(creationAndUpdate)
                .lastUpdateDate(creationAndUpdate)
                .duration(30)
                .price(1.4)
                .tags(tags)
                .build();

        CertificateDto expected = CertificateDto.builder()
                .id(1)
                .name("name")
                .description("xd")
                .createDate(creationAndUpdate)
                .lastUpdateDate(creationAndUpdate)
                .duration(30)
                .price(1.4)
                .tags(tagDtos)
                .build();

        CertificateDto actual = mapper.certificateToDto(source);

        assertEquals(expected, actual);
    }

    @Test
    void dtoToCertificate_equalDtos_equalEntities() {
        TagDto exTagDto = TagDto.builder()
                .id(1)
                .name("tag")
                .build();
        Tag exTag = Tag.builder()
                .id(1)
                .name("tag")
                .build();
        LocalDateTime creationAndUpdate = LocalDateTime.now();
        Set<TagDto> tagDtos = new HashSet<>();
        Set<Tag> tags = new HashSet<>();
        tags.add(exTag);
        tagDtos.add(exTagDto);
        CertificateDto source = CertificateDto.builder()
                .id(1)
                .name("name")
                .description("xd")
                .createDate(creationAndUpdate)
                .lastUpdateDate(creationAndUpdate)
                .duration(30)
                .price(1.4)
                .tags(tagDtos)
                .build();

        Certificate expected = Certificate.builder()
                .id(1)
                .name("name")
                .description("xd")
                .createDate(creationAndUpdate)
                .lastUpdateDate(creationAndUpdate)
                .duration(30)
                .price(1.4)
                .tags(tags)
                .build();

        Certificate actual = mapper.dtoToCertificate(source);

        assertEquals(expected, actual);
    }
}