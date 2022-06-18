package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.UserDto;

import java.util.Optional;

public interface OrderService extends CRUDService<OrderDto> {
    Page<OrderDto> findByUserId(long id, Pageable pageable);
    long getSequenceNextVal();
    long getSequenceCurrVal();
    void updateSequence(long val);
}
