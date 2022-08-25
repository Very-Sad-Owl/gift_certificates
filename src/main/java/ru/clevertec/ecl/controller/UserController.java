package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.service.UserService;

import static ru.clevertec.ecl.interceptor.common.UrlPaths.*;

/**
 * Controller  class for /users path
 *
 * Provides REST interface for read only operations on {@link ru.clevertec.ecl.entity.baseentities.User}
 * entity using {@link ru.clevertec.ecl.dto.UserDto} as DTO.
 *
 * See also {@link org.springframework.web.bind.annotation.RestController}.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(PATH_USERS)
public class UserController {

    /**
     * Service class object to perform corresponding business logic on
     * {@link ru.clevertec.ecl.entity.baseentities.User} entities.
     */
    private final UserService userService;

    /**
     * Performs {@link ru.clevertec.ecl.entity.baseentities.User} search.
     *
     * @param pageable pagination parameters
     * @param params DTO containing filtering parameters
     * @return paged collection of users
     */
    @GetMapping(value = ACTION_FIND_ALL)
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> findAllUsers(Pageable pageable, UserDto params) {
        return userService.getAll(params, pageable);
    }

    /**
     * Performs {@link ru.clevertec.ecl.entity.baseentities.User} search by given id.
     *
     * @param id required user's id
     * @return user with given id
     */
    @GetMapping(value = ACTION_FIND)
    @ResponseStatus(HttpStatus.OK)
    public UserDto findUserById(@RequestParam Long id) {
        return userService.findById(id);
    }
}
