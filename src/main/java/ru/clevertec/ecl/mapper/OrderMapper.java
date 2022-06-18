package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.Order;

@Component
@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto orderToDto(Order order);
    Order dtoToOrder(OrderDto dto);

//    @AfterMapping
//    default void map( @MappingTarget OrderDto target, Order source ,
//                      @Context UserService userService,
//                      @Context CertificateService certificateService) {
//        try {
//            target.setUser(userService.findById(source.getUserId()));
//            target.setCertificate((certificateService.findById(source.getCertificateId())));
//        } catch (NotFoundException e) {
//            return;
//        }
//    }
}
