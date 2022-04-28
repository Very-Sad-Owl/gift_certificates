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
import ru.clevertec.ecl.exception.crud.UpdatingException;
import ru.clevertec.ecl.exception.crud.notfound.NotFoundException;
import ru.clevertec.ecl.exception.crud.notfound.TagNotFoundException;
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
    public OrderDto save(OrderDto e) {
        if (e.getCertificateId() != 0) {
            CertificateDto existing = certificateService.findById(e.getCertificateId());
            e.setCertificate(existing);
        } else {
            CertificateDto custom = certificateService.save(e.getCertificate());
            e.setCertificate(custom);
        }
        if (e.getUserId() == 0) {
            throw new NotFoundException(); //TODO: validation
        }
        UserDto user = userService.findById(e.getUserId());
        e.setUser(user);
        e.setPurchaseTime(LocalDateTime.now());
        e.calculatePrice();
        return mapper.orderToDto(repository.save(mapper.dtoToOrder(e)));
    }

    @Override
    public OrderDto findById(long id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            return mapper.orderToDto(order.get());
        } else {
            throw new NotFoundException(id + "");
        }
    }

    @Override
    public Page<OrderDto> getAll(OrderDto params, Pageable pageable) {
        if (params.getUserId() != 0) {
            return findByUserId(params.getUserId(), pageable);
        }
        return repository.findAll(pageable).map(mapper::orderToDto);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public OrderDto update(OrderDto dto) {
        try {
            repository.findById(dto.getId())
                    .map(mapper::orderToDto)
                    .orElseThrow(() -> new UpdatingException(new TagNotFoundException(dto.getId() + "")));
            return mapper.orderToDto(repository.save(mapper.dtoToOrder(dto)));
        } catch (DataIntegrityViolationException e) {
            throw new UpdatingException(e);
        } catch (EmptyResultDataAccessException e) {
            throw new UpdatingException(new TagNotFoundException(dto.getId()+""));
        }
    }

    @Override
    public Page<OrderDto> findByUserId(long id, Pageable pageable) {
        return repository.findByUserId(id, pageable)
                .map(mapper::orderToDto);
    }
}
