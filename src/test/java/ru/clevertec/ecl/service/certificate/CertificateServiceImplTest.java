package ru.clevertec.ecl.service.certificate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.*;
import ru.clevertec.ecl.service.*;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.health.HealthCheckerConfiguration;
import ru.clevertec.ecl.service.order.OrderServiceTestConfiguration;
import ru.clevertec.ecl.service.tag.TagServiceTestConfiguration;
import ru.clevertec.ecl.service.user.UserServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogService;
import ru.clevertec.ecl.service.health.HealthCheckerService;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {CertificateServiceTestConfiguration.class, TagMapperImpl.class,
        UserMapperImpl.class, OrderMapperImpl.class,
        CertificateMapperImpl.class,
        TagServiceTestConfiguration.class,
        CommitLogDbConfiguration.class,
        CertificateServiceTestConfiguration.class,
        PrimaryDbConfiguration.class,
        CommitLogConfiguration.class,
        HealthCheckerConfiguration.class,
        ClusterPropertiesConfiguration.class,
        UserServiceTestConfiguration.class,
        CommonConfiguration.class,
        OrderServiceTestConfiguration.class})
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CertificateServiceImplTest {

    @Autowired
    TagService tagService;
    @Autowired
    CertificateService service;
    @Autowired
    TagMapper tagMapper;
    @Autowired
    CertificateMapper certificateMapper;
    @Autowired
    CommitLogService commitLogService;
    @Autowired
    HealthCheckerService healthCheckerService;
    @Autowired
    ClusterProperties properties;

    @BeforeAll
    static void setUp() {
        LocalDateTime timeCreatedAndUpdated =
                LocalDateTime.parse("2011-12-03T10:15:40", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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
        exampleOneCopy = CertificateDto.builder()
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
                .tags(new HashSet<>(Collections.singletonList(exTagThree)))
                .build();
    }

    @Test
    @Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = {"/tags.sql", "/certificates.sql", "/certificate-tags.sql"})
    @Sql(scripts = {"/set_sequence.sql"})
    void save() {
        CertificateDto saved = service.save(newCertificate);

        assertEquals(4, saved.getId());
    }

    @Test
    void findById_existingId_equalDtos() {
        long id = 2;

        CertificateDto actual = service.findById(id);

        assertEquals(exampleTwo, actual);
    }

    @Test
    void findById_nonExistingId_exc() {
        long id = 0;

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.findById(id)
        );

        assertNotNull(thrown);
    }

    @Test
    void getAll_filtersNotSpecifiedUnpaged_allPages() {
        List<CertificateDto> expected = Arrays.asList(exampleOne, exampleTwo, exampleThree);

        List<CertificateDto> actual = service.getAll(new CertificateDto(),
                PageRequest.of(0, 3)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_filtersNotSpecifiedSecondPageOf2Elements_oneElement() {
        List<CertificateDto> expected = Collections.singletonList(exampleThree);

        List<CertificateDto> actual = service.getAll(new CertificateDto(),
                PageRequest.of(1, 2)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNamePartUnpaged_allPages() {
        List<CertificateDto> expected = Arrays.asList(exampleTwo, exampleThree);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder().name("m").build(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withDescriptionPartUnpaged_allPages() {
        List<CertificateDto> expected = Collections.singletonList(exampleOne);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder().description("happy").build(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNamePartFirstPageOfSize1_oneElement() {
        List<CertificateDto> expected = Collections.singletonList(exampleTwo);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder().name("m").build(),
                PageRequest.of(0, 1)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNameAndDescriptionPartUnpaged_allPages() {
        List<CertificateDto> expected = Collections.singletonList(exampleThree);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .name("m")
                        .description("may")
                        .build(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNameAndDescriptionPartFirstPageOfSize3_oneElements() {
        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .name("m")
                        .description("may")
                        .build(),
                PageRequest.of(0, 3))
                .getContent();

        assertEquals(exampleThree, actual.get(0));
    }

    @Test
    void getAll_withTagUnpdaged_allPages() {
        List<CertificateDto> expected = new ArrayList<>(Arrays.asList(exampleTwo, exampleThree));

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .filteringTags(new HashSet<>(Collections.singletonList("holiday")))
                        .build(),
                PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withTagFirstPageOfSize1_oneElement() {
        List<CertificateDto> expected = Collections.singletonList(exampleTwo);

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .filteringTags(new HashSet<>(Collections.singletonList("holiday")))
                        .build(),
                PageRequest.of(0, 1, Sort.by(Sort.Order.asc("id")))).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withNonExistingTag_pagedData() {
        List<CertificateDto> expected = new ArrayList<>();

        List<CertificateDto> actual = service.getAll(CertificateDto.builder()
                        .name("xd")
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
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.delete(id)
        );
        assertNotNull(thrown);
    }

    @Test
    void update_onlyDescription_sameIdAndUpdatedDescription() {
        long originalId = 1;
        String originalDescription = "happy birthday";
        exampleOneCopy.setDescription("new descr");
        exampleOneCopy = service.update(exampleOneCopy);
        assertTrue(exampleOneCopy.getId() == originalId && !exampleOneCopy.getDescription().equals(originalDescription));
    }

    @Test
    void update_nameAndTag_sameIdAndUpdatedNameAndTag() {
        long originalId = 1;
        String originalName = "hpb";
        exampleOneCopy.setName("TbI DED");
        exampleOneCopy.getTags().add(exTagThree);
        exampleOneCopy = service.update(exampleOneCopy);
        assertTrue(exampleOneCopy.getId() == originalId
                && !exampleOneCopy.getName().equals(originalName)
                && exampleOneCopy.getTags().contains(exTagThree));
    }


    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static CertificateDto exampleOne;
    private static CertificateDto exampleOneCopy;
    private static CertificateDto exampleTwo;
    private static CertificateDto exampleThree;
    private static CertificateDto newCertificate;
}