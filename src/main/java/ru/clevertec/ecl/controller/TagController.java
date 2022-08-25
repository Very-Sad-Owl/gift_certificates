package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.service.TagService;

import static ru.clevertec.ecl.interceptor.common.UrlPaths.*;

/**
 * Controller  class for /tags path
 *
 * Provides REST interface for basic CRUD logic operations on {@link ru.clevertec.ecl.entity.baseentities.Tag}
 * entity using {@link ru.clevertec.ecl.dto.TagDto} as DTO.
 *
 * See also {@link org.springframework.web.bind.annotation.RestController}.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(PATH_TAGS)
public class TagController {

    /**
     * Service class object to perform corresponding business logic on
     * {@link ru.clevertec.ecl.entity.baseentities.Tag} entities.
     */
    private final TagService tagService;

    /**
     * Performs {@link ru.clevertec.ecl.entity.baseentities.Tag} search.
     *
     * @param pageable pagination parameters
     * @param params DTO containing filtering parameters
     * @return paged collection of tags
     */
    @GetMapping(value = ACTION_FIND_ALL)
    @ResponseStatus(HttpStatus.OK)
    public Page<TagDto> findAllTags(Pageable pageable, TagDto params) {
        return tagService.getAll(params, pageable);
    }

    /**
     * Performs {@link ru.clevertec.ecl.entity.baseentities.Tag} search by given id.
     *
     * @param id required order's id
     * @return tag with given id
     */
    @GetMapping(value = ACTION_FIND)
    @ResponseStatus(HttpStatus.OK)
    public TagDto findTagById(@RequestParam Integer id) {
        return tagService.findById(id);
    }

    /**
     * Saves new {@link ru.clevertec.ecl.entity.baseentities.Tag}.
     *
     * @param tag tag data to be saved
     * @return saved entity
     */
    @PostMapping(value = ACTION_SAVE)
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto saveTag(@RequestBody TagDto tag) {
        return tagService.save(tag);
    }

    /**
     * Updates existing {@link ru.clevertec.ecl.entity.baseentities.Tag}.
     *
     * @param tag tag data to be updated
     * @return updated tag entity
     */
    @PutMapping(value = ACTION_UPDATE)
    @ResponseStatus(HttpStatus.OK)
    public TagDto updateTag(@RequestBody TagDto tag) {
        return tagService.update(tag);
    }

    /**
     * Removes {@link ru.clevertec.ecl.entity.baseentities.Tag} by given id.
     *
     * @param id required tag's id
     */
    @DeleteMapping(ACTION_DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTag(@RequestParam Integer id) {
        tagService.delete(id);
    }
}
