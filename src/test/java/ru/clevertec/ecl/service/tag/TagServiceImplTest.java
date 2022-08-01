package ru.clevertec.ecl.service.tag;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapperImpl;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.mapper.TagMapperImpl;
import ru.clevertec.ecl.service.CommitLogDbConfiguration;
import ru.clevertec.ecl.service.CommonConfiguration;
import ru.clevertec.ecl.service.PrimaryDbConfiguration;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.certificate.CertificateServiceTestConfiguration;
import ru.clevertec.ecl.service.commitlog.CommitLogConfiguration;
import ru.clevertec.ecl.service.health.HealthCheckerConfiguration;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;
import ru.clevertec.ecl.util.health.HealthCheckerService;
import ru.clevertec.ecl.webutils.clusterproperties.ClusterPropertiesConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {TagServiceTestConfiguration.class, TagMapperImpl.class, CertificateMapperImpl.class,
        CommitLogDbConfiguration.class,
        PrimaryDbConfiguration.class,
        CommitLogConfiguration.class,
        HealthCheckerConfiguration.class,
        ClusterPropertiesConfiguration.class,
        CertificateServiceTestConfiguration.class,
        CommonConfiguration.class})
@Sql(scripts = {"/clean_entity_db.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/tags.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TagServiceImplTest {

    @Autowired
    TagService service;
    @Autowired
    TagMapper tagMapper;
    @Autowired
    CommitLogWorker commitLogWorker;
    @Autowired
    HealthCheckerService healthCheckerService;
    @Autowired
    ClusterProperties properties;

    @BeforeAll
    static void setUp() {
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
                .id(4)
                .name("new tag")
                .build();
        newTagToSave = TagDto.builder()
                .name("saved")
                .build();
        duplicateTag = TagDto.builder()
                .name("holiday")
                .build();
    }

    @Test
    @Sql(scripts = {"/set_sequence.sql"})
    void save_newTag_generatedIdNot0() {
        TagDto actual = service.save(newTagToSave);

        assertTrue(actual.getId() != 0);
    }

    @Test
    void save_newTagWithExistingName_savingException() {
        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> service.save(duplicateTag)
        );

        assertSame(thrown.getClass(), DataIntegrityViolationException.class);
    }

    @Test
    void findById_existingId_dto() {
        TagDto actual = service.findById(exTagOne.getId());

        assertEquals(actual, exTagOne);
    }

    @Test
    void findById_nonExistingId_notFoundException() {
        long id = 0;

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.findById(id)
        );

        assertSame(thrown.getClass(), NotFoundException.class);
    }

    @Test
    void getAll_noFilters_page() {
        List<TagDto> expected = Arrays.asList(exTagOne, exTagTwo, exTagThree);

        List<TagDto> actual = service.getAll(new TagDto(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }


    @Test
    void delete_existingId_noExceptionsThrown() {
        long id = 3;
        assertDoesNotThrow(() -> service.delete(id));
    }

    @Test
    void delete_nonExistingId_exception() {
        long id = 0;
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.delete(id)
        );
        assertSame(thrown.getClass(), NotFoundException.class);
    }

    @Test
    void update_partial_updatedDto() {
        TagDto expected = TagDto.builder()
                .id(exTagOne.getId())
                .name("updated")
                .build();

        TagDto actual = service.update(expected);

        assertEquals(expected, actual);
    }

    @Test
    void update_nonExistingOrigin_updateException() {
        TagDto tagWithNonExistingId = TagDto.builder().id(0).name("any").build();
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.update(tagWithNonExistingId)
        );
        assertEquals(0, thrown.getCauseId());
    }

    @Test
    void findByName_christmas_christmasTag() {
        String name = exTagTwo.getName(); //christmas

        TagDto expected = exTagTwo;

        TagDto actual = service.findByName(name);

        assertEquals(expected, actual);
    }

    @Test
    void findByName_noSuchTag_null() {
        String name = "xd";

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.findByName(name)
        );

        assertNotNull(thrown);
    }

    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static TagDto newTag;
    private static TagDto newTagToSave;
    private static TagDto duplicateTag;
}