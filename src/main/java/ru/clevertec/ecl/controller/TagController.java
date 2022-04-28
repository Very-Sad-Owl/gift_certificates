package ru.clevertec.ecl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.service.TagService;

import java.util.Locale;

import static ru.clevertec.ecl.util.Constant.*;

@Slf4j
@RestController
@RequestMapping("/tags")
public class TagController {

    private final MessageSource messageSource;
    private final TagService tagService;

    @Autowired
    public TagController(MessageSource messageSource, TagService tagService) {
        this.messageSource = messageSource;
        this.tagService = tagService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String welcome(Locale loc) {
        return messageSource.getMessage("label.guide", null, loc);
    }

    @GetMapping(value = ACTION_LOG, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Page<TagDto> log(Pageable pageable, TagDto params) {
        return tagService.getAll(params, pageable);
    }

    @GetMapping(value = ACTION_FIND, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public TagDto find(@RequestParam Integer id) {
        return tagService.findById(id);
    }

    @PostMapping(value = ACTION_SAVE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto save(@RequestBody TagDto params) {
        return tagService.save(params);
    }

    @PatchMapping(value = ACTION_PATCH, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public TagDto patch(@RequestBody TagDto params) {
        return tagService.update(params);
    }

    @DeleteMapping(ACTION_DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam Integer id) {
        tagService.delete(id);
    }
    //localhost:8080/certificates/log?name=asc&price=desc&tag_name='100% power'&part_of_name=happy&part_of_descr=for
}
