package ru.clevertec.ecl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.Order;
import ru.clevertec.ecl.exception.InvalidArgException;
import ru.clevertec.ecl.exception.crud.*;
import ru.clevertec.ecl.mapper.OrderMapper;
import ru.clevertec.ecl.repository.OrderRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.util.matcherhelper.MatcherBuilder;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class OrderServiceImpl
        extends AbstractService<OrderDto, Order, OrderRepository>
        implements OrderService {

    private final OrderMapper mapper;
    private final CertificateService certificateService;
    private final UserService userService;

    @Autowired
    public OrderServiceImpl(OrderRepository repository, MatcherBuilder<OrderDto> filterMatcher, OrderMapper mapper, CertificateService certificateService, UserService userService) {
        super(repository, filterMatcher);
        this.mapper = mapper;
        this.certificateService = certificateService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public OrderDto save(OrderDto order) {
        return mapper.orderToDto(repository.save(mapper.dtoToOrder(order)));
    }

    @Override
    public OrderDto makeOrder(OrderDto order) {
        if (order.getCertificateId() != 0) {
            CertificateDto existing = certificateService.findById(order.getCertificateId());
            order.setCertificate(existing);
        } else {
            CertificateDto custom = certificateService.save(order.getCertificate());
            order.setCertificate(custom);
        }
        if (order.getUserId() == 0) {
            throw new InvalidArgException(); //TODO: validation
        }
        try {
            UserDto user = userService.findById(order.getUserId());
            order.setUser(user);
            order.setPurchaseTime(LocalDateTime.now());
            order.calculatePrice();
            return mapper.orderToDto(repository.save(mapper.dtoToOrder(order)));
        } catch (DataIntegrityViolationException e) {
            throw new SavingException(e, order.getId());
        }
    }

    @Override
    public OrderDto findById(long id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            return mapper.orderToDto(order.get());
        } else {
            throw new NotFoundException(id);
        }
    }

    @Override
    public Page<OrderDto> getAll(OrderDto params, Pageable pageable) {
        if (params.getUserId() != 0) {
            return findByUserId(params.getUserId(), pageable);
        } else {
            return repository.findAll(pageable).map(mapper::orderToDto);
        }
    }

    @Override
    public void delete(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeletionException(id);
        }
    }

    @Override
    public OrderDto update(OrderDto dto) {
        try {
            repository.findById(dto.getId())
                    .map(mapper::orderToDto)
                    .orElseThrow(() -> new UpdatingException(dto.getId()));
            return mapper.orderToDto(repository.save(mapper.dtoToOrder(dto)));
        } catch (DataIntegrityViolationException | EmptyResultDataAccessException e) {
            throw new UpdatingException(e, dto.getId());
        }
    }

    @Override
    public Page<OrderDto> findByUserId(long id, Pageable pageable) {
        return repository.findByUserId(id, pageable)
                .map(mapper::orderToDto);
    }
}
