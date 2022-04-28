package ru.clevertec.ecl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.User;
import ru.clevertec.ecl.service.UserService;

import java.util.Locale;

import static ru.clevertec.ecl.util.Constant.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final MessageSource messageSource;
    private final UserService userService;

    @Autowired
    public UserController(MessageSource messageSource, UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String welcome(Locale loc) {
        return messageSource.getMessage("label.guide", null, loc);
    }

    @GetMapping(value = ACTION_LOG, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> log(Pageable pageable, UserDto params) {
        return userService.getAll(params, pageable);
    }

    @GetMapping(value = ACTION_FIND, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public UserDto find(@RequestParam Integer id) {
        return userService.findById(id);
    }


    //localhost:8080/certificates/log?name=asc&price=desc&tag_name='100% power'&part_of_name=happy&part_of_descr=for
}
