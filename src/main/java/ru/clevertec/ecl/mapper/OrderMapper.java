package ru.clevertec.ecl.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.Order;
import ru.clevertec.ecl.entity.User;

@Component
@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto orderToDto(Order order);
    Order dtoToOrder(OrderDto dto);
}
