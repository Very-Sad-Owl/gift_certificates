package ru.clevertec.ecl.service;

import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.service.impl.AbstractService;

import java.util.Set;

/**
 * An interface declaring methods {@link ru.clevertec.ecl.service.impl.CertificateServiceImpl} must implement.
 *
 * See also {@link CRUDService}.
 *
 * @author Olga Mailychko
 *
 */
public interface CertificateService extends CRUDService<CertificateDto> {
    /**
     * Method moves {@link ru.clevertec.ecl.entity.baseentities.Certificate}'s sequence to next value
     *
     * @return next value of {@link ru.clevertec.ecl.entity.baseentities.Certificate}'s sequence
     */
    long getSequenceNextVal();
    /**
     * @return {@link ru.clevertec.ecl.entity.baseentities.Certificate} sequence's current value
     */
    long getSequenceCurrVal();
    /**
     * Method updates {@link ru.clevertec.ecl.entity.baseentities.Certificate}'s sequence to given value
     *
     * @param val value sequence must be set to
     */
    void updateSequence(long val);
    /**
     * Method finds all {@link Certificate} entities from storage with given {@link ru.clevertec.ecl.entity.baseentities.Tag}.
     *
     * @param tag {@link TagDto} object representing {@link ru.clevertec.ecl.entity.baseentities.Tag} value
     *                          that all found entities must include.
     * @return set of {@link CertificateDto} objects containing given {@link TagDto} object.
     */
    Set<CertificateDto> findAllCertificatesWithTag(TagDto tag);
}
