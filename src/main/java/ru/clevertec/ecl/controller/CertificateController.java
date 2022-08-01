package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.service.CertificateService;

import static ru.clevertec.ecl.interceptor.common.UrlPaths.*;

/**
 * Controller  class for /certificates path
 *
 * Provides REST interface for basic CRUD logic operations on {@link ru.clevertec.ecl.entity.baseentities.Certificate}
 * entity using {@link ru.clevertec.ecl.dto.CertificateDto} as DTO.
 *
 * See also {@link org.springframework.web.bind.annotation.RestController}.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(PATH_CERTIFICATES)
public class CertificateController {

    /**
     * Service class object to perform corresponding business logic on
     * {@link ru.clevertec.ecl.entity.baseentities.Certificate} entities.
     */
    private final CertificateService certificateService;

    /**
     * Performs certificates search with given filters.
     *
     * @param pageable pagination parameters
     * @param params DTO containing filtering parameters
     * @return paged collection of certificates
     */
    @GetMapping(value = ACTION_FIND_ALL)
    @ResponseStatus(HttpStatus.OK)
    public Page<CertificateDto> findAllCertificates(Pageable pageable, CertificateDto params) {
        return certificateService.getAll(params, pageable);
    }

    /**
     * Performs {@link ru.clevertec.ecl.entity.baseentities.Certificate}  search by given id.
     *
     * @param id required certificate's id
     * @return certificate with given id
     */
    @GetMapping(value = ACTION_FIND)
    @ResponseStatus(HttpStatus.OK)
    public CertificateDto findCertificate(@RequestParam Integer id) { //TODO: name
        return certificateService.findById(id);
    }

    /**
     * Saves new certificate.
     *
     * @param certificate certificate data to be saved
     * @return saved entity
     */
    @PostMapping(value = ACTION_SAVE)
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateDto saveCertificate(@RequestBody CertificateDto certificate) {
        return certificateService.save(certificate);
    }

    /**
     * Updates existing certificate.
     *
     * @param certificate certificate data to be updated
     * @return updated certificate entity
     */
    @PutMapping(value = ACTION_UPDATE)
    @ResponseStatus(HttpStatus.OK)
    public CertificateDto updateCertificate(@RequestBody CertificateDto certificate) {
        return certificateService.update(certificate);
    }

    /**
     * Removes certificate by given id.
     *
     * @param id required certificate's id
     */
    @DeleteMapping(ACTION_DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteCertificate(@RequestParam Integer id) {
        certificateService.delete(id);
    }

}
