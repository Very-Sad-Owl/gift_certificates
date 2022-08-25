package ru.clevertec.ecl.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.CertificateFilterDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.CertificateRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.OrderService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.commitlog.CommitLogService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.CERTIFICATES_SEQUENCE;
import static ru.clevertec.ecl.service.common.DatabaseConstants.ALIAS_CERTIFICATES;

/**
 * Service class providing CRUD operations on {@link Certificate}.
 *
 * See also {@link AbstractService}, {@link CertificateService}.
 *
 * @author Olga Mailychko
 *
 */
@Service
public class CertificateServiceImpl
        extends AbstractService<CertificateDto, Certificate, CertificateRepository>
        implements CertificateService {

    private final CertificateMapper mapper;
    private final TagMapper tagMapper;
    private final TagService tagService;
    private final CommitLogService commitLogService;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    public CertificateServiceImpl(ClusterProperties properties, CertificateRepository repository,
                                  CertificateMapper mapper, TagMapper tagMapper, TagService tagService,
                                  CommitLogService commitLogService, ObjectMapper objectMapper,
                                  @Lazy OrderService orderService) {
        super(properties, repository);
        this.mapper = mapper;
        this.tagMapper = tagMapper;
        this.tagService = tagService;
        this.commitLogService = commitLogService;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @Override
    public CertificateDto save(CertificateDto toSave) {
        Set<TagDto> tags = new HashSet<>();
        for (TagDto el : toSave.getTags()) {
            tags.add(tagService.findById(el.getId()));
        }
        toSave.setTags(tags);
        toSave.setCreateDate(LocalDateTime.now());
        toSave.setLastUpdateDate(LocalDateTime.now());
        CertificateDto saved = mapper.certificateToDto(repository.save(mapper.dtoToCertificate(toSave)));

        CommitLog logNode = commitLogService.formLogNode(Action.SAVE,
                saved,
                ALIAS_CERTIFICATES);
        commitLogService.writeAction(logNode);

        return saved;
    }

    @Override
    public CertificateDto findById(long id) {
        return repository.findById(id)
                .map(mapper::certificateToDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<CertificateDto> getAll(CertificateDto filterCertificate, Pageable pageable) {
        List<CertificateDto> result = new ArrayList<>();
        Pageable limit = PageRequest.ofSize((pageable.getPageNumber() + 1) * pageable.getPageSize());
        CertificateFilterDto filter = mapper.dtoToFilter(filterCertificate);
        List<CertificateFilterDto.RequestedFilter> requestedFilters = filter.getRequestedFilters();
        if (requestedFilters.isEmpty() || requestedFilters.get(0).equals(CertificateFilterDto.RequestedFilter.NONE)) {
            return repository.findAll(pageable).map(mapper::certificateToDto);
        }
        for (CertificateFilterDto.RequestedFilter fieldFilter : requestedFilters) {
            switch (fieldFilter) {
                case TAG:
                    List<CertificateDto> foundByTags = new ArrayList<>(repository
                            .findAllByTagsIn(filter.getFilteringTags(), pageable.getPageSize()))
                            .stream()
                            .map(mapper::certificateToDto)
                            .collect(Collectors.toList());
                    result.addAll(foundByTags);
                    break;
                case NAME_AND_DESCRIPTION:
                    result.addAll(repository
                            .findAllByDescriptionContainingAndNameContaining
                                    (filter.getDescription(), filter.getName(), limit).stream()
                            .map(mapper::certificateToDto)
                            .collect(Collectors.toSet()));
                    break;
                case NAME:
                    result.addAll(repository
                            .findAllByNameContaining(filter.getName(), limit).stream()
                            .map(mapper::certificateToDto)
                            .collect(Collectors.toSet()));
                    break;
                case DESCRIPTION:
                    result.addAll(repository
                            .findAllByDescriptionContaining(filter.getDescription(), limit).stream()
                            .map(mapper::certificateToDto)
                            .collect(Collectors.toSet()));
                    break;
            }
        }
        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public void delete(long id) {
        Set<OrderDto> relatedOrders = orderService.findOrdersWithCertificateById(id);
        relatedOrders.forEach(order -> {
            order.setCertificateId(0);
            orderService.update(order);
        });
        CertificateDto toDelete = findById(id);
        repository.deleteById(id);
        CommitLog logNode = commitLogService
                .formLogNode(Action.DELETE,
                toDelete,
                ALIAS_CERTIFICATES);
        commitLogService.writeAction(logNode);
    }

    @Override
    public CertificateDto update(CertificateDto dto) {
        repository.findById(dto.getId()).orElseThrow(() -> new NotFoundException(dto.getId()));
        dto.setLastUpdateDate(LocalDateTime.now());
        CertificateDto updated = mapper
                .certificateToDto(repository.save(mapper.dtoToCertificate(dto)));
        CommitLog logNode = commitLogService.formLogNode(Action.UPDATE,
                updated,
                ALIAS_CERTIFICATES);
        commitLogService.writeAction(logNode);
        return updated;
    }

    @Override
    public Set<CertificateDto> findAllCertificatesWithTag(TagDto tag) {
        return repository.findAllByTagsContains(tagMapper.dtoToTag(tag))
                .stream().map(mapper::certificateToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public long getSequenceNextVal() {
        return repository.getSeqNextVal(CERTIFICATES_SEQUENCE);
    }

    @Override
    public void updateSequence(long val) {
        repository.setSeqVal(CERTIFICATES_SEQUENCE, val);
    }

    @Override
    public long getSequenceCurrVal() {
        return repository.getCurrentCertificateSequence();
    }

}
