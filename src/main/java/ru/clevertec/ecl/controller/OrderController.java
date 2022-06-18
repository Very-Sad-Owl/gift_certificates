package ru.clevertec.ecl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.SequenceDto;
import ru.clevertec.ecl.service.OrderService;

import java.util.Locale;

import static ru.clevertec.ecl.util.Constant.*;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final MessageSource messageSource;
    private final OrderService orderService;

    @Autowired
    public OrderController(MessageSource messageSource, OrderService orderService) {
        this.messageSource = messageSource;
        this.orderService = orderService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String welcome(Locale loc) {
        return messageSource.getMessage("label.guide", null, loc);
    }

    @GetMapping(value = ACTION_FIND, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public OrderDto find(@RequestParam Integer id) {
        return orderService.findById(id);
    }

    @PostMapping(value = "/buy", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto save(@RequestBody OrderDto params, @RequestParam(required = false, defaultValue = "false") boolean redirected) {
        return orderService.save(params);
    }

    @PostMapping(value = ACTION_UPDATE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public OrderDto patch(@RequestBody OrderDto params) {
        return orderService.update(params);
    }

    @DeleteMapping(ACTION_DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam Integer id) {
        orderService.delete(id);
    }

    @GetMapping(value = "/seq/next") //TODO: dlinnoe slovo
    public long getSeqNextVal() {
        return orderService.getSequenceNextVal();
    }

    @GetMapping(value = "/seq/current")
    public long getSeqCurrVal() {
        return orderService.getSequenceCurrVal();
    }

//    @PutMapping(value = "/seq/set", produces = {MediaType.APPLICATION_JSON_VALUE}) //TODO:
    @PostMapping(value = "/seq/set", produces = {MediaType.APPLICATION_JSON_VALUE})
    public void setSeqNextVal(@RequestBody SequenceDto sequence) {
        orderService.updateSequence(sequence.getValue());
    }

}
