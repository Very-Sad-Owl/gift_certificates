package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.baseentities.Order;

@Component
@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto orderToDto(Order order);
    Order dtoToOrder(OrderDto dto);
}
