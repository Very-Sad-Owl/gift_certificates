package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.baseentities.Order;

import java.util.Set;

/**
 * An interface declaring methods {@link ru.clevertec.ecl.service.impl.OrderServiceImpl} must implement.
 *
 * See also {@link CRUDService}.
 *
 * @author Olga Mailychko
 *
 */
public interface OrderService extends CRUDService<OrderDto> {
    /**
     * Method finds all {@link Order} entities from storage with given {@link ru.clevertec.ecl.entity.baseentities.User}'s id.
     *
     * @param id id of {@link ru.clevertec.ecl.entity.baseentities.User} which must be containing in {@link Order}
     *                          that all found entities must include.
     * @return paged collection of found {@link Order} entities represented as {@link OrderDto}
     */
    Page<OrderDto> findByUserId(long id, Pageable pageable);
    /**
     * Method finds all {@link Order} entities from storage with given {@link Certificate}'s id.
     *
     * @param certificate {@link Certificate} represented as {@link CertificateDto} which must be containing in {@link Order}
     *                          that all found entities must include.
     * @return set of found {@link Order} entities represented as {@link OrderDto}
     */
    Set<OrderDto> findOrdersWithCertificate(CertificateDto certificate);
    /**
     * Method moves {@link ru.clevertec.ecl.entity.baseentities.Order}'s sequence to next value
     *
     * @return next value of {@link ru.clevertec.ecl.entity.baseentities.Order}'s sequence
     */
    long getSequenceNextVal();
    /**
     * @return {@link ru.clevertec.ecl.entity.baseentities.Order} sequence's current value
     */
    long getSequenceCurrVal();
    /**
     * Method updates {@link ru.clevertec.ecl.entity.baseentities.Order}'s sequence to given value
     *
     * @param val value sequence must be set to
     */
    void updateSequence(long val);
}
