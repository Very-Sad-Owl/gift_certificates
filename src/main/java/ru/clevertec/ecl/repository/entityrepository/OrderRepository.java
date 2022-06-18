package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Order;

@Repository
public interface OrderRepository extends CommonRepository<Order> {
    Page<Order> findByUserId(long user_id, Pageable pageable);
}
