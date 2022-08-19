package ru.clevertec.ecl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.entity.baseentities.Order;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.OrderMapper;
import ru.clevertec.ecl.mapper.UserMapper;
import ru.clevertec.ecl.repository.entityrepository.OrderRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.UserService;
import ru.clevertec.ecl.service.commitlog.CommitLogService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.service.common.DatabaseConstants.*;
import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.*;

/**
 * Service class providing CRUD operations on {@link Order}.
 *
 * See also {@link AbstractService}, {@link OrderService}.
 *
 * @author Olga Mailychko
 *
 */
@Service
public class OrderServiceImpl
        extends AbstractService<OrderDto, Order, OrderRepository>
        implements OrderService {

    private final OrderMapper mapper;
    private final CertificateMapper certificateMapper;
    private final UserMapper userMapper;
    private final CertificateService certificateService;
    private final UserService userService;
    private final CommitLogService commitLogService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderServiceImpl(ClusterProperties properties, OrderRepository repository, OrderMapper mapper,
                            CertificateMapper certificateMapper, UserMapper userMapper,
                            CertificateService certificateService, CommitLogService commitLogService,
                            ObjectMapper objectMapper, UserService userService) {
        super(properties, repository);
        this.mapper = mapper;
        this.certificateMapper = certificateMapper;
        this.userMapper = userMapper;
        this.certificateService = certificateService;
        this.commitLogService = commitLogService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public OrderDto save(OrderDto toSave) {
        CertificateDto certificate = certificateService.findById(toSave.getCertificateId());
        UserDto user = userService.findById(toSave.getUserId());
        toSave.setCertificate(certificate);
        if (toSave.getPurchaseTime() == null) {
            toSave.setPurchaseTime(LocalDateTime.now());
        }
        toSave.calculatePrice();
        OrderDto saved = mapper.orderToDto(repository.save(mapper.dtoToOrder(toSave)));
        toSave.setId(saved.getId());
        saved.setCertificate(certificate);
        saved.setUser(user);

        CommitLog logNode = commitLogService.formLogNode(Action.SAVE,
                saved,
                ALIAS_ORDERS);
        commitLogService.writeAction(logNode);
        return saved;
    }

    @Override
    public OrderDto findById(long id) {
        OrderDto found = repository.findById(id)
                .map(mapper::orderToDto)
                .orElseThrow(() -> new NotFoundException(id));
        found.setCertificate(certificateService.findById(found.getCertificateId()));
        found.setUser(userService.findById(found.getUserId()));
        return found;
    }

    @Override
    public Page<OrderDto> getAll(OrderDto filter, Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::orderToDto)
                .map(t -> {
                    t.setCertificate(certificateService.findById(t.getCertificateId()));
                    t.setUser(userService.findById(t.getUserId()));
                    return t;
                });

    }

    @Override
    public void delete(long id) {
        OrderDto toDelete = findById(id);
        repository.deleteById(id);
        CommitLog logNode = commitLogService.formLogNode(Action.DELETE,
                toDelete,
                ALIAS_ORDERS);
        commitLogService.writeAction(logNode);
    }

    @Override
    public OrderDto update(OrderDto dto) {
        repository.findById(dto.getId())
                .map(mapper::orderToDto)
                .orElseThrow(NotFoundException::new);
        CertificateDto requestedCertificate = certificateService.findById(dto.getCertificateId());
        UserDto requestedUser = userService.findById(dto.getUserId());
        dto.setPrice(requestedCertificate.getPrice());
        OrderDto updated = mapper.orderToDto(repository.save(mapper.dtoToOrder(dto)));
        updated.setCertificate(requestedCertificate);
        updated.setUser(requestedUser);
        CommitLog logNode = commitLogService.formLogNode(Action.UPDATE,
                updated,
                ALIAS_ORDERS);
        commitLogService.writeAction(logNode);
        return updated;
    }

    @Override
    public Page<OrderDto> findByUserId(long id, Pageable pageable) {
        return repository.findByUserId(id, pageable)
                .map(found -> {
                    OrderDto foundDto = mapper.orderToDto(found);
                    foundDto.setCertificate(certificateService.findById(found.getCertificateId()));
                    foundDto.setUser(userService.findById(found.getUserId()));
                    return foundDto;
                });
    }

    @Override
    public Set<OrderDto> findOrdersWithCertificateById(long id) {
        return repository.findAllByCertificateId(id)
                .stream()
                .map(found -> {
                    OrderDto foundDto = mapper.orderToDto(found);
                    CertificateDto certificate = certificateService.findById(found.getCertificateId());
                    UserDto user = userService.findById(found.getUserId());
                    foundDto.setCertificate(certificate);
                    foundDto.setUser(user);
                    foundDto.setCertificateId(certificate.getId());
                    foundDto.setUserId(user.getId());
                    return foundDto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public long getSequenceNextVal() {
        return repository.getSeqNextVal(ORDERS_SEQUENCE);
    }

    @Override
    public void updateSequence(long val) {
        repository.setSeqVal(ORDERS_SEQUENCE, val);
    }

    @Override
    public long getSequenceCurrVal() {
        return repository.getCurrentOrderSequence();
    }
}
