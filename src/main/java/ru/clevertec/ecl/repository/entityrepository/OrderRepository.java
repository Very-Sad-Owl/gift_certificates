package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.baseentities.Order;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;

import java.util.Set;

import static ru.clevertec.ecl.repository.UsedQuery.ORDER_SEQ_CURR_VAL;

/**
 * Repository for {@link Order} entity.
 *
 * @author Olga Mailychko
 *
 */
@Repository
public interface OrderRepository extends CommonRepository<Order> {
    Page<Order> findByUserId(long user_id, Pageable pageable);
    Set<Order> findAllByCertificateId(long certificateId);
    @Query(value = ORDER_SEQ_CURR_VAL, nativeQuery = true)
    long getCurrentOrderSequence();
}
