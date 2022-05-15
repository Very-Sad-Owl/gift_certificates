package ru.clevertec.ecl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.util.health.HealthChecker;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/")
public class Controller {

    private final MessageSource messageSource;
    private final HealthChecker healthChecker;

    @Autowired
    public Controller(MessageSource messageSource, HealthChecker healthChecker) {
        this.messageSource = messageSource;
        this.healthChecker = healthChecker;
    }

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> welcome(Locale loc) {
        return new ResponseEntity<>(messageSource.getMessage("label.guide", null, loc),
                HttpStatus.OK);
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> healthCheckCluster() {
        return healthChecker.healthCheckEndpoint();
    }

}
