package ru.clevertec.ecl.service;

import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.exception.NotFoundException;

/**
 * An interface declaring methods {@link ru.clevertec.ecl.service.impl.TagServiceImpl} must implement.
 *
 * See also {@link CRUDService}.
 *
 * @author Olga Mailychko
 *
 */
public interface TagService extends CRUDService<TagDto> {
    /**
     * Method finds {@link Tag} entity represented as {@link TagDto} with given name.
     *
     * @param name name of desired {@link Tag}
     * @return found {@link Tag} entity represented as {@link TagDto}
     * @throws NotFoundException if there is no eny {@link Tag} with fiven name
     */
    TagDto findByName(String name);
    /**
     * Method finds top {@link Tag} popular among {@link ru.clevertec.ecl.entity.baseentities.User}s.
     *
     * @return found {@link Tag} entity represented as {@link TagDto}
     * @throws NotFoundException if there is no any {@link Tag} in storage
     */
    TagDto findTopUserMoreCommonTag();
    /**
     * Method moves {@link ru.clevertec.ecl.entity.baseentities.Tag}'s sequence to next value
     *
     * @return next value of {@link ru.clevertec.ecl.entity.baseentities.Tag}'s sequence
     */
    long getSequenceNextVal();
    /**
     * @return {@link ru.clevertec.ecl.entity.baseentities.Tag} sequence's current value
     */
    long getSequenceCurrVal();
    /**
     * Method updates {@link ru.clevertec.ecl.entity.baseentities.Tag}'s sequence to given value
     *
     * @param val value sequence must be set to
     */
    void updateSequence(long val);
}
