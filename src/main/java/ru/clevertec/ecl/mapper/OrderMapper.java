package ru.clevertec.ecl.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.Order;
import ru.clevertec.ecl.entity.User;

@Component
@Mapper(componentModel = "spring", uses = CertificateMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "certificateId", ignore = true)
    OrderDto orderToDto(Order order);
    @InheritInverseConfiguration
    Order dtoToOrder(OrderDto dto);
}
