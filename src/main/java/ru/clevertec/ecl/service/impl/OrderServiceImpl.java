package ru.clevertec.ecl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.dto.UserDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
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
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;

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
    private final CommitLogWorker commitLogWorker;
    private final ObjectMapper objectMapper;

    @Autowired
    public OrderServiceImpl(ClusterProperties properties, OrderRepository repository, OrderMapper mapper,
                            CertificateMapper certificateMapper, UserMapper userMapper,
                            CertificateService certificateService, CommitLogWorker commitLogWorker,
                            ObjectMapper objectMapper, UserService userService) {
        super(properties, repository);
        this.mapper = mapper;
        this.certificateMapper = certificateMapper;
        this.userMapper = userMapper;
        this.certificateService = certificateService;
        this.commitLogWorker = commitLogWorker;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    /**
     * Performs saving logic on {@link Order} using {@link OrderDto} data.
     *
     * @param toSave DTO object containing saving data
     * @return saved {@link OrderDto} object
     */
    @SneakyThrows
    @Override
    @Transactional
    public OrderDto save(OrderDto toSave) {
        CertificateDto certificate = certificateService.findById(toSave.getCertificateId());
        UserDto user = userService.findById(toSave.getUserId());
        toSave.setCertificate(certificate);
        toSave.setUser(user);
        toSave.setPurchaseTime(LocalDateTime.now());
        toSave.calculatePrice();
        OrderDto res = mapper.orderToDto(repository.save(mapper.dtoToOrder(toSave)));
        toSave.setId(res.getId());

        CommitLog logNode = commitLogWorker.formLogNode(Action.SAVE,
                objectMapper.writeValueAsString(toSave),
                ALIAS_ORDERS);
        commitLogWorker.writeAction(logNode);
        return toSave;
    }

    /**
     * Finds {@link Order} object by id.
     *
     * @param id id of needed {@link Order}
     * @return found {@link Order} entity as {@link OrderDto} object
     */
    @Override
    public OrderDto findById(long id) {
        OrderDto found = repository.findById(id)
                .map(mapper::orderToDto)
                .orElseThrow(() -> new NotFoundException(id));
        found.setCertificate(certificateService.findById(found.getCertificateId()));
        found.setUser(userService.findById(found.getUserId()));
        return found;
    }

    /**
     * Finds all {@link Order} entities from storage.
     *
     * @param filter {@link CertificateDto} object containing filtering fields
     * @param pageable {@link Pageable} object storing pagination data
     * @return paged collection of all found {@link Order} entities represented as {@link OrderDto} objects
     */
    @Override
    public Page<OrderDto> getAll(OrderDto filter, Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::orderToDto)//TODO:
                .map(t -> {
                    t.setCertificate(certificateService.findById(t.getCertificateId()));
                    t.setUser(userService.findById(t.getUserId()));
                    return t;
                });

    }

    /**
     * Method performs removing {@link Order} entity with given id from storage.
     *
     * @param id id of entity to be removed
     */
    @SneakyThrows
    @Override
    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
        CommitLog logNode = commitLogWorker.formLogNode(Action.DELETE,
                objectMapper.writeValueAsString(id),
                ALIAS_ORDERS);
        commitLogWorker.writeAction(logNode);
    }

    /**
     * Method performs editing logic on {@link Order} entity. All data contains in {@link OrderDto}.
     *
     * @param dto {@link OrderDto} object that is basically original entity's data with updated fields.
     * @return updated data as {@link OrderDto} object
     */
    @SneakyThrows
    @Transactional
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
        CommitLog logNode = commitLogWorker.formLogNode(Action.UPDATE,
                objectMapper.writeValueAsString(updated),
                ALIAS_ORDERS);
        commitLogWorker.writeAction(logNode);
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
    public Set<OrderDto> findOrdersWithCertificate(CertificateDto certificate) {
        return repository.findAllByCertificateId(certificate.getId())
                .stream()
                .map(found -> {
                    OrderDto foundDto = mapper.orderToDto(found);
                    foundDto.setCertificate(certificateService.findById(found.getCertificateId()));
                    foundDto.setUser(userService.findById(found.getUserId()));
                    return foundDto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public long getSequenceNextVal() {
        return repository.getSeqNextVal(ORDERS_SEQUENCE);
    }

    @Override
    @Transactional
    public void updateSequence(long val) {
        repository.setSeqVal(ORDERS_SEQUENCE, val);
    }

    @Override
    public long getSequenceCurrVal() {
        return repository.getCurrentOrderSequence();
    }
}
