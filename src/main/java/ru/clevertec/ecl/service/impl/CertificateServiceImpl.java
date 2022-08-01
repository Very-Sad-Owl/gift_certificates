package ru.clevertec.ecl.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.CertificateFilterDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.exception.ErrorResponse;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.CertificateRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;

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
    private final CommitLogWorker commitLogWorker;
    private final ObjectMapper objectMapper;

    public CertificateServiceImpl(ClusterProperties properties, CertificateRepository repository,
                                  CertificateMapper mapper, TagMapper tagMapper, TagService tagService,
                                  CommitLogWorker commitLogWorker, ObjectMapper objectMapper) {
        super(properties, repository);
        this.mapper = mapper;
        this.tagMapper = tagMapper;
        this.tagService = tagService;
        this.commitLogWorker = commitLogWorker;
        this.objectMapper = objectMapper;
    }

    /**
     * Performs saving logic on {@link Certificate} using {@link CertificateDto} data.
     *
     * @param toSave DTO object containing saving data
     * @return saved {@link CertificateDto} object
     */
    @SneakyThrows
    @Transactional
    @Override
    public CertificateDto save(CertificateDto toSave) {
        Set<TagDto> tags = new HashSet<>();
        for (TagDto el : toSave.getTags()) {
            tags.add(tagService.getOrSaveIfExists(el));
        }
        toSave.setTags(tags);
        toSave.setCreateDate(LocalDateTime.now());
        toSave.setLastUpdateDate(LocalDateTime.now());
        CertificateDto saved = mapper.certificateToDto(repository.save(mapper.dtoToCertificate(toSave)));

        CommitLog logNode = commitLogWorker.formLogNode(Action.SAVE,
                objectMapper.writeValueAsString(saved),
                ALIAS_CERTIFICATES);
        commitLogWorker.writeAction(logNode);

        return saved;
    }

    /**
     * Finds {@link Certificate} entity by id in storage.
     *
     * @param id id of needed {@link Certificate}
     * @return found {@link Certificate} entity as {@link CertificateDto} object
     */
    @Override
    public CertificateDto findById(long id) {
        return repository.findById(id)
                .map(mapper::certificateToDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    /**
     * Finds all {@link Certificate} entities from storage with given filters.
     *
     * @param filterCertificate {@link CertificateDto} object containing filtering fields
     * @param pageable {@link Pageable} object storing pagination data
     * @return paged collection of all found {@link Certificate} entities represented as {@link CertificateDto} objects
     * meeting all requirements
     */
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

    /**
     * Method performs removing {@link Certificate} entity with given id from storage.
     *
     * @param id id of entity to be removed
     */
    @Transactional
    @SneakyThrows
    @Override
    public void delete(long id) {
        repository.deleteById(id);
        CommitLog logNode = commitLogWorker.formLogNode(Action.DELETE,
                objectMapper.writeValueAsString(id),
                ALIAS_CERTIFICATES);
        commitLogWorker.writeAction(logNode);
    }

    /**
     * Method performs editing logic on {@link Certificate} entity. All data contains in {@link CertificateDto}.
     *
     * @param dto {@link CertificateDto} object that is basically original entity's data with updated fields.
     * @return updated data as {@link CertificateDto} object
     */
    @Transactional
    @SneakyThrows
    @Override
    public CertificateDto update(CertificateDto dto) {
        repository.findById(dto.getId()).orElseThrow(() -> new NotFoundException(dto.getId()));
        dto.setLastUpdateDate(LocalDateTime.now());
        CertificateDto updated = mapper
                .certificateToDto(repository.save(mapper.dtoToCertificate(dto)));
        CommitLog logNode = commitLogWorker.formLogNode(Action.UPDATE,
                objectMapper.writeValueAsString(updated),
                ALIAS_CERTIFICATES);
        commitLogWorker.writeAction(logNode);
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
