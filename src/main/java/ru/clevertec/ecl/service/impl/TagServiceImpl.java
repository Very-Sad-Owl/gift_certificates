package ru.clevertec.ecl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.OrderDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.baseentities.Order;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.exception.UndefinedException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.TagRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.util.commitlog.CommitLogWorker;

import java.util.Set;

import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.TAGS_SEQUENCE;
import static ru.clevertec.ecl.service.common.DatabaseConstants.ALIAS_TAGS;

/**
 * Service class providing CRUD operations on {@link Tag}.
 *
 * See also {@link AbstractService}, {@link TagService}.
 *
 * @author Olga Mailychko
 *
 */
@Service
public class TagServiceImpl
        extends AbstractService<TagDto, Tag, TagRepository>
        implements TagService {

    private final ClusterProperties clusterProperties;
    private final CertificateService certificateService;
    private final TagMapper mapper;
    private final CommitLogWorker commitLogWorker;
    private final ObjectMapper objectMapper;

    public TagServiceImpl(ClusterProperties properties, TagRepository repository,
                          @Lazy CertificateService certificateService, TagMapper mapper,
                          CommitLogWorker commitLogWorker, ObjectMapper objectMapper) {
        super(properties, repository);
        this.clusterProperties = properties;
        this.certificateService = certificateService;
        this.mapper = mapper;
        this.commitLogWorker = commitLogWorker;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @SneakyThrows
    @Override
    public TagDto save(TagDto toSave) {
        TagDto saved = mapper.tagToDto(repository.save(mapper.dtoToTag(toSave)));
        CommitLog logNode = commitLogWorker.formLogNode(Action.SAVE,
                objectMapper.writeValueAsString(saved),
                ALIAS_TAGS);
        commitLogWorker.writeAction(logNode);
        return saved;
    }

    @Override
    public TagDto findById(long id) {
        return repository.findById(id)
                .map(mapper::tagToDto)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<TagDto> getAll(TagDto filter, Pageable pageable) {
        return repository.findAll(pageable).map(mapper::tagToDto);
    }

    @Transactional
    @SneakyThrows
    @Override
    public void delete(long id) {
        Tag requested = repository.findById(id).orElseThrow(() -> new NotFoundException(id));
        Set<CertificateDto> allCertificatesWithTag = certificateService
                .findAllCertificatesWithTag(mapper.tagToDto(requested));
        for (CertificateDto certificateDto : allCertificatesWithTag) {
            certificateDto.getTags().remove(mapper.tagToDto(requested));
            certificateService.update(certificateDto);
        }
        repository.deleteById(id);
        CommitLog logNode = commitLogWorker.formLogNode(Action.DELETE,
                objectMapper.writeValueAsString(id),
                ALIAS_TAGS);
        commitLogWorker.writeAction(logNode);
    }

    @Transactional
    @SneakyThrows
    @Override
    public TagDto update(TagDto dto) {
        TagDto updated = repository.findById(dto.getId())
                .map(found -> mapper.tagToDto(repository.save(mapper.dtoToTag(dto))))
                .orElseThrow(NotFoundException::new);
        CommitLog logNode = commitLogWorker.formLogNode(Action.UPDATE,
                objectMapper.writeValueAsString(updated),
                ALIAS_TAGS);
        commitLogWorker.writeAction(logNode);
        return updated;
    }

    @Override
    public TagDto findByName(String name) {
        return repository.findByName(name)
                .map(mapper::tagToDto)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public TagDto getOrSaveIfExists(TagDto tag) {
        return repository.findById(tag.getId())
                .map(mapper::tagToDto)
                .orElseGet(() -> {
                    CommitLog logNode = null;
                    try {
                        logNode = commitLogWorker
                                .formLogNode(Action.SAVE, objectMapper.writeValueAsString(tag), ALIAS_TAGS);
                    } catch (JsonProcessingException e) {
                        throw new UndefinedException(e);
                    }
                    commitLogWorker.writeAction(logNode);
                    return mapper.tagToDto(repository.save(mapper.dtoToTag(tag)));
                });
    }

    @Override
    public TagDto findTopUserMoreCommonTag() {
        return repository.findTopUserMoreCommonTag()
                .map(mapper::tagToDto)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public long getSequenceNextVal() {
        return repository.getSeqNextVal(TAGS_SEQUENCE);
    }

    @Override
    public void updateSequence(long val) {
        repository.setSeqVal(TAGS_SEQUENCE, val);
    }

    @Override
    public long getSequenceCurrVal() {
        return repository.getCurrentTagSequence();
    }

}
