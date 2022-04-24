package ru.clevertec.ecl.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.App;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.UnsupportedFilterException;
import ru.clevertec.ecl.exception.crud.DeletionException;
import ru.clevertec.ecl.exception.crud.SavingException;
import ru.clevertec.ecl.exception.crud.UpdatingException;
import ru.clevertec.ecl.exception.crud.notfound.TagNotFoundException;
import ru.clevertec.ecl.repository.TagRepository;
import ru.clevertec.ecl.service.TagService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@AutoConfigureTestDatabase
@SpringBootTest(classes = App.class, webEnvironment= SpringBootTest.WebEnvironment.NONE)
@Transactional
class TagServiceImplTest {

    @Autowired
    private TagService service;

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
    void save_newTag_generatedIdNot0() {
        TagDto actual = service.save(newTagToSave);

        assertTrue(actual.getId() != 0);
    }

    @Test
    void save_newTagWithExistingName_savingException() {
        SavingException thrown = assertThrows(
                SavingException.class,
                () -> service.save(duplicateTag)
        );

        assertSame(thrown.getCause().getClass(), DataIntegrityViolationException.class);
    }

    @Test
    void findById_existingId_dto() {
        long id = 1;

        TagDto actual = service.findById(id);

        assertEquals(actual, exTagOne);
    }

    @Test
    void findById_nonExistingId_notFoundException() {
        long id = 0;

        TagNotFoundException thrown = assertThrows(
                TagNotFoundException.class,
                () -> service.findById(id),
                id+""
        );

        assertTrue(thrown.getMessage().contains(id+""));
    }

    @Test
    void getAll_noFilters_page() {
        List<TagDto> expected = Arrays.asList(exTagOne, exTagTwo, exTagThree);

        List<TagDto> actual = service.getAll(new TagDto(),
                PageRequest.of(0, 10)).getContent();

        assertEquals(expected, actual);
    }

    @Test
    void getAll_withUnsupportedFilters_page() {
        UnsupportedFilterException thrown = assertThrows(
                UnsupportedFilterException.class,
                () -> service.getAll(TagDto.builder().name("holiday").build(),
                        PageRequest.of(0, 10))
        );

        assertNotNull(thrown);
    }

    @Test
    void delete_existingId_noExceptionsThrown() {
        long id = 1;
        assertDoesNotThrow(() -> service.delete(id));
    }

    @Test
    void delete_nonExistingId_exception() {
        long id = 0;
        DeletionException thrown = assertThrows(
                DeletionException.class,
                () -> service.delete(id),
                id+""
        );
        assertTrue(thrown.getCause().getMessage().contains(id+""));
    }

    @Test
    void update_partial_updatedDto() {
        TagDto expected = TagDto.builder()
                .id(exTagOne.getId())
                .name(exTagTwo.getName())
                .build();

        TagDto actual = service.update(expected);

        assertEquals(expected, actual);
    }

    @Test
    void update_nonExistingOrigin_updateException() {
        TagDto tagWithNonExistingId = TagDto.builder().id(0).name("any").build();
        UpdatingException thrown = assertThrows(
                UpdatingException.class,
                () -> service.update(tagWithNonExistingId)
        );
        assertTrue(thrown.getCause().getMessage().contains(tagWithNonExistingId.getId()+""));
    }

    @Test
    void findByName_christmas_christmasTag() {
        String name = exTagTwo.getName(); //christmas

        TagDto expected = exTagTwo;

        TagDto actual = service.findByName(name).get();

        assertEquals(expected, actual);
    }

    @Test
    void findByName_noSuchTag_null() {
        String name = "xd";

        Optional<TagDto> actual = service.findByName(name);

        assertFalse(actual.isPresent());
    }

//    @Test
//    void getOrSave_birthdayTag_get() {
//        TagRepository repository = Mockito.mock(TagRepository.class);
//        service.getOrSave(exTagOne);
//        Mockito.verify(repository, times(1)).findByName(any());
//    }
//
//    @Test
//    void getOrSave_newTag_save() {
//        TagRepository repository = Mockito.mock(TagRepository.class);
//        service.getOrSave(newTag);
//        Mockito.verify(repository, times(1)).save(any());
//    }

    private static TagDto exTagOne;
    private static TagDto exTagTwo;
    private static TagDto exTagThree;
    private static TagDto newTag;
    private static TagDto newTagToSave;
    private static TagDto duplicateTag;
}