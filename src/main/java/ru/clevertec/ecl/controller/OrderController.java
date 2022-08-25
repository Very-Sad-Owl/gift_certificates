package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.service.OrderService;

import static ru.clevertec.ecl.interceptor.common.UrlPaths.*;

/**
 * Controller  class for /orders path
 *
 * Provides REST interface for basic CRUD logic operations on {@link ru.clevertec.ecl.entity.baseentities.Order}
 * entity using {@link ru.clevertec.ecl.dto.OrderDto} as DTO.
 *
 * See also {@link org.springframework.web.bind.annotation.RestController}.
 *
 * @author Olga Mailychko
 *
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(PATH_ORDERS)
public class OrderController {

    /**
     * Service class object to perform corresponding business logic on
     * {@link ru.clevertec.ecl.entity.baseentities.Order} entities.
     */
    private final OrderService orderService;

    /**
     * Performs {@link ru.clevertec.ecl.entity.baseentities.Order} search by given id.
     *
     * @param id required order's id
     * @return order with given id
     */
    @GetMapping(value = ACTION_FIND)
    @ResponseStatus(HttpStatus.OK)
    public OrderDto findOrder(@RequestParam Integer id) {
        return orderService.findById(id);
    }

    /**
     * Saves new {@link ru.clevertec.ecl.entity.baseentities.Order}.
     *
     * @param order order data to be saved
     * @return saved entity
     */
    @PostMapping(value = ACTION_BUY)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto saveOrder(@RequestBody OrderDto order) {
        return orderService.save(order);
    }

    /**
     * Updates existing {@link ru.clevertec.ecl.entity.baseentities.Order}.
     *
     * @param order certificate data to be updated
     * @return updated order entity
     */
    @PutMapping(value = ACTION_UPDATE)
    @ResponseStatus(HttpStatus.OK)
    public OrderDto updateOrder(@RequestBody OrderDto order) {
        return orderService.update(order);
    }

    /**
     * Removes {@link ru.clevertec.ecl.entity.baseentities.Order} by given id.
     *
     * @param id required order's id
     */
    @DeleteMapping(ACTION_DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam Integer id) {
        orderService.delete(id);
    }

    /**
     * Method moving order table's sequence to next value.
     *
     * @return order sequence's next value
     */
    @GetMapping(value = ACTION_MOVE_SEQUENCE)
    public long getSequenceNextValue() {
        return orderService.getSequenceNextVal();
    }

    /**
     * @return order sequence's current value
     */
    @GetMapping(value = ACTION_SEQUENCE_CURRENT)
    public long getSequenceCurrValue() {
        return orderService.getSequenceCurrVal();
    }

    /**
     * Method sets order sequence to the specified value.
     *
     * @param value value sequence must be set on
     */
    @PostMapping(value = ACTION_SET_SEQUENCE)
    public void setSequenceValue(@RequestBody Integer value) {
        orderService.updateSequence(value);
    }

}
