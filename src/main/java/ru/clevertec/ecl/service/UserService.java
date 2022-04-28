package ru.clevertec.ecl.service;

import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserService extends CRUDService<UserDto> {
    Optional<UserDto> findByName(String name);
}
