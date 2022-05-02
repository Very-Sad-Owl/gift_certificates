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
import ru.clevertec.ecl.service.CertificateService;

import java.util.Locale;

import static ru.clevertec.ecl.util.Constant.*;

@Slf4j
@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private final MessageSource messageSource;
    private final CertificateService certificateService;

    @Autowired
    public CertificateController(MessageSource messageSource, CertificateService certificateService) {
        this.messageSource = messageSource;
        this.certificateService = certificateService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String welcome(Locale loc) {
        return messageSource.getMessage("label.guide", null, loc);
    }

    @GetMapping(value = ACTION_FIND_ALL, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Page<CertificateDto> findAll(Pageable pageable, CertificateDto params) {
        return certificateService.getAll(params, pageable);
    }

    @GetMapping(value = ACTION_FIND, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public CertificateDto find(@RequestParam Integer id) {
        return certificateService.findById(id);
    }

    @PostMapping(value = ACTION_SAVE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateDto save(@RequestBody CertificateDto params) {
        return certificateService.save(params);
    }

    @PutMapping(value = ACTION_PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public CertificateDto put(@RequestBody CertificateDto params) {
        return certificateService.update(params);
    }

    @DeleteMapping(ACTION_DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam Integer id) {
        certificateService.delete(id);
    }

    //localhost:8080/certificates/log?name=asc&price=desc&tag_name='100% power'&part_of_name=happy&part_of_descr=for
}
