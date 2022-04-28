package ru.clevertec.ecl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Order;
import ru.clevertec.ecl.entity.User;

import java.util.Optional;

@Repository
public interface OrderRepository extends CommonRepository<Order> {
    Page<Order> findByUserId(long user_id, Pageable pageable);
}
