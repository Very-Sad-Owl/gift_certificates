package ru.clevertec.ecl.service;

import ru.clevertec.ecl.dto.UserDto;

/**
 * An interface declaring methods {@link ru.clevertec.ecl.service.impl.UserServiceImpl} must implement.
 *
 * See also {@link CRUDService}.
 *
 * @author Olga Mailychko
 *
 */
public interface UserService extends CRUDService<UserDto> {
}
