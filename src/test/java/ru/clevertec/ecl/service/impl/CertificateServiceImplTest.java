package ru.clevertec.ecl.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.App;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.service.CertificateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureTestDatabase
@SpringBootTest(classes = App.class, webEnvironment= SpringBootTest.WebEnvironment.NONE)
@Transactional
class CertificateServiceImplTest {

    @Autowired
    CertificateService service;

    @BeforeEach
    void setUp() {
        LocalDateTime timeCreatedAndUpdated =
                LocalDateTime.parse("2022-04-21T00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        exTagOne = TagDto.builder()
                .id(1)
                .name("birthday")
                .build();
        exTagTwo = TagDto.builder()
                .id(2)
                .name("christmas")
                .build();
        exTagThree = TagDto.builder()
                .id(3)
                .name("holiday")
                .build();
        newTag = TagDto.builder()
                .name("religion")
                .build();
        exampleOne = CertificateDto.builder()
                .id(1)
                .name("hpb")
                .description("happy birthday")
                .price(2.1)
                .duration(60)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagOne)))
                .build();
        exampleTwo = CertificateDto.builder()
                .id(2)
                .name("mc")
                .description("merry christmas")
                .price(5.6)
                .duration(10)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Arrays.asList(exTagTwo, exTagThree)))
                .build();
        exampleThree = CertificateDto.builder()
                .id(3)
                .name("may 1")
                .description("may the first")
                .price(10.5)
                .duration(1)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
                .tags(new HashSet<>(Collections.singletonList(exTagThree)))
                .build();
        newCertificate = CertificateDto.builder()
                .name("happy easter")
                .description("XDXDXD")
                .price(777)
                .duration(2)
                .createDate(timeCreatedAndUpdated)
                .lastUpdateDate(timeCreatedAndUpdated)
//                .tags(new HashSet<>(Arrays.asList(exTagThree, newTag)))
                .tags(new HashSet<>(Arrays.asList(newTag, exTagThree)))
                .build();
    }

    @Test
    void save() {
        CertificateDto saved = service.save(newCertificate);

        assertTrue(saved.getId() > 0);
    }

    @Test
    void findById_existingId_equalDtos() {
        long id = 1;

        CertificateDto actual = service.findById(id);

        assertEquals(actual, exampleOne);
    }

    @Test
    void findById_nonExistingId_exc() {
        long id = 0;

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.findById(id),
                id+""
        );

        assertSame(id, thrown.getCauseId());
    }

    @Test
    void getAll_filtersNotSpecified_allPages() {
        List<CertificateDto> expected = Arrays.asList(exampleOne, exampleTwo, exampleThree);

        List<CertificateDto> actual = service.getAll(new CertificateDto(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNamePart_page() {
        List<CertificateDto> expected = Arrays.asList(exampleTwo, exampleThree);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder().name("m").build(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNameAndDescriptionPart_page() {
        List<CertificateDto> expected = Collections.singletonList(exampleThree);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .name("m")
                        .description("may")
                        .build(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withTag_pagedData() {
        List<CertificateDto> expected = Arrays.asList(exampleTwo, exampleThree);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .filteringTags(new HashSet<>(Arrays.asList("holiday")))
                        .build(),
                PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNonExistingTag_pagedData() {
        List<CertificateDto> expected = new ArrayList<>();

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .filteringTags(new HashSet<>(Arrays.asList("xd")))
                        .build(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void delete_existingId_noExceptionsThrown() {
        long id = 1;
        assertDoesNotThrow(() -> service.delete(id));
    }

    @Test
    void delete_nonExistingId_exception() {
        long id = 0;
        EmptyResultDataAccessException thrown = assertThrows(
                EmptyResultDataAccessException.class,
                () -> service.delete(id),
                id+""
        );
        assertSame(thrown.getClass(), EmptyResultDataAccessException.class);
    }

    @Test
    void update_onlyDescription_sameIdAndUpdatedDescription() {
        long originalId = 1;
        String originalDescription = "happy birthday";
        exampleOne.setDescription("new descr");
        exampleOne = service.update(exampleOne);
        assertTrue(exampleOne.getId() == originalId && !exampleOne.getDescription().equals(originalDescription));
    }

    @Test
    void update_nameAndTag_sameIdAndUpdatedNameAndTag() {
        long originalId = 1;
        String originalName = "hpb";
        exampleOne.setName("TbI DED");
        exampleOne.getTags().add(exTagThree);
        exampleOne = service.update(exampleOne);
        assertTrue(exampleOne.getId() == originalId
                && !exampleOne.getName().equals(originalName)
                && exampleOne.getTags().contains(exTagThree));
    }

    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static TagDto newTag;
    private static CertificateDto exampleOne;
    private static CertificateDto exampleTwo;
    private static CertificateDto exampleThree;
    private static CertificateDto newCertificate;
}