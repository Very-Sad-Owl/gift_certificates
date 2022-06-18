package ru.clevertec.ecl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.Order;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.OrderMapper;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.repository.entityrepository.OrderRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.util.matcherhelper.MatcherBuilder;

import java.time.LocalDateTime;


@Service
public class OrderServiceImpl
        extends AbstractService<OrderDto, Order, OrderRepository>
        implements OrderService {

    private final OrderMapper mapper;
    private final CertificateMapper certificateMapper;
    private final UserMapper userMapper;
    private final CertificateService certificateService;
    //    private final CertificateRepository certificateRepository;
    private final UserService userService;
//    private final UserRepository userRepository;


    @Autowired
    public OrderServiceImpl(OrderRepository repository, MatcherBuilder<OrderDto> filterMatcher, OrderMapper mapper, CertificateMapper certificateMapper, UserMapper userMapper, CertificateService certificateService, UserService userService) {
        super(repository, filterMatcher);
        this.mapper = mapper;
        this.certificateMapper = certificateMapper;
        this.userMapper = userMapper;
        this.certificateService = certificateService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public OrderDto save(OrderDto order) {
//        CertificateDto certificate = certificateService.findById(order.getCertificateId());
//        UserDto user = userService.findById(order.getUserId());
//        order.setCertificate(certificate);
//        order.setUser(user);
        order.setPurchaseTime(LocalDateTime.now());
//        order.calculatePrice();
        order.setPrice(1);
        OrderDto res = mapper.orderToDto(repository.save(mapper.dtoToOrder(order)));
        order.setId(res.getId());
        return order;
    }

    @Override
    public OrderDto findById(long id) {
        return repository.findById(id)
                .map(mapper::orderToDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<OrderDto> getAll(OrderDto params, Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::orderToDto)//TODO:
                .map(t -> {
                    t.setCertificate(certificateService.findById(t.getCertificateId()));
                    t.setUser(userService.findById(t.getUserId()));
                    return t;
                });

    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public OrderDto update(OrderDto dto) {
        return repository.findById(dto.getId())
                .map(found ->
                        mapper.orderToDto(repository.save(found)))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Page<OrderDto> findByUserId(long id, Pageable pageable) {
        return repository.findByUserId(id, pageable)
                .map(t -> mapper.orderToDto(t));
    }

    @Override
    public long getSequenceNextVal() {
        return repository.getSeqNextVal("seq");
    }

    @Override
    @Transactional
    public void updateSequence(long val) {
        repository.setSeqVal("seq", val);
    }

    @Override
    public long getSequenceCurrVal() {
//        return repository.currSeqVal("seq");
        return repository.currSeqVal();
    }
}
