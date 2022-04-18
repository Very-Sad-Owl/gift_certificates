package ru.clevertec.ecl.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.Tag;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CertificateMapperTest {

    @Autowired
    CertificateMapper mapper;

    @Test
    void certificateToDto() {
        Tag exTag = Tag.builder()
                .id(1)
                .name("tag")
                .build();
        Set<Tag> tags = new HashSet<>();
        tags.add(exTag);
        Certificate source = Certificate.builder()
                .id(1)
                .name("name")
                .description("xd")
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .duration(30)
                .price(1.4)
                .tags(tags)
                .build();
        CertificateDto actual = mapper.certificateToDto(source);
        assertNotNull(actual);
    }

    @Test
    void dtoToCertificate() {
        TagDto exTag = TagDto.builder()
                .id(1)
                .name("tag")
                .build();
        Set<TagDto> tags = new HashSet<>();
        tags.add(exTag);
        CertificateDto source = CertificateDto.builder()
                .id(1)
                .name("name")
                .description("xd")
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .duration(30)
                .price(1.4)
                .tags(tags)
                .build();
        Certificate actual = mapper.dtoToCertificate(source);
        assertNotNull(actual);
    }
}