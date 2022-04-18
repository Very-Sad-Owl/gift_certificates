package ru.clevertec.ecl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/")
public class Controller {

    private final MessageSource messageSource;

    @Autowired
    public Controller(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> welcome(Locale loc) {
        return new ResponseEntity<>(messageSource.getMessage("label.guide", null, loc),
                HttpStatus.OK);
    }

}
