package ru.clevertec.ecl.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.baseentities.Order;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OrderMapperImpl.class)
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void orderToDtoTest() {
        LocalDateTime now = LocalDateTime.now();
        Order entity = Order.builder().id(1).price(20).certificateId(4).userId(3).purchaseTime(now).build();

        OrderDto expected = OrderDto.builder().id(1).price(20).certificateId(4).userId(3).purchaseTime(now).build();

        OrderDto actual = orderMapper.orderToDto(entity);

        assertEquals(expected, actual);
    }

    @Test
    public void dtoToOrderTest() {
        LocalDateTime now = LocalDateTime.now();
        OrderDto dto = OrderDto.builder().id(1).price(20).certificateId(4).userId(3).purchaseTime(now).build();

        Order expected = Order.builder().id(1).price(20).certificateId(4).userId(3).purchaseTime(now).build();

        Order actual = orderMapper.dtoToOrder(dto);

        assertEquals(expected, actual);
    }
}
