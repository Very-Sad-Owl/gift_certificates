package ru.clevertec.ecl.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.baseentities.Order;
import ru.clevertec.ecl.entity.baseentities.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserMapperImpl.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void userToDtoTest() {
        User entity = User.builder().id(1).name("Oleg").surname("Olegov").build();

        UserDto expected = UserDto.builder().id(1).name("Oleg").surname("Olegov").build();

        UserDto actual = userMapper.userToDto(entity);

        assertEquals(expected, actual);
    }

    @Test
    public void dtoToUser() {
        UserDto dto = UserDto.builder().id(1).name("Oleg").surname("Olegov").build();

        User expected = User.builder().id(1).name("Oleg").surname("Olegov").build();

        User actual = userMapper.dtoToUser(dto);

        assertEquals(expected, actual);
    }
}
