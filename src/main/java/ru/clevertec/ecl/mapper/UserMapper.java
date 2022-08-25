package ru.clevertec.ecl.mapper;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.baseentities.User;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToDto(User user);
    User dtoToUser(UserDto dto);
}
