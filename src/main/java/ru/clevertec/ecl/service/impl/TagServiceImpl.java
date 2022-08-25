package ru.clevertec.ecl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.postgresql.util.PSQLException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.commitlogentities.Action;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.TagRepository;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.commitlog.CommitLogService;

import java.util.Set;

import static ru.clevertec.ecl.entity.baseentities.common.SequenceTitles.TAGS_SEQUENCE;
import static ru.clevertec.ecl.service.common.DatabaseConstants.ALIAS_TAGS;

/**
 * Service class providing CRUD operations on {@link Tag}.
 * <p>
 * See also {@link AbstractService}, {@link TagService}.
 *
 * @author Olga Mailychko
 */
@Service
public class TagServiceImpl
        extends AbstractService<TagDto, Tag, TagRepository>
        implements TagService {

    private final ClusterProperties clusterProperties;
    private final CertificateService certificateService;
    private final TagMapper mapper;
    private final CommitLogService commitLogService;
    private final ObjectMapper objectMapper;

    public TagServiceImpl(ClusterProperties properties, TagRepository repository,
                          @Lazy CertificateService certificateService, TagMapper mapper,
                          CommitLogService commitLogService, ObjectMapper objectMapper) {
        super(properties, repository);
        this.clusterProperties = properties;
        this.certificateService = certificateService;
        this.mapper = mapper;
        this.commitLogService = commitLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    public TagDto save(TagDto toSave) {
        TagDto saved = mapper.tagToDto(repository.save(mapper.dtoToTag(toSave)));
        CommitLog logNode = commitLogService.formLogNode(Action.SAVE,
                saved,
                ALIAS_TAGS);
        commitLogService.writeAction(logNode);
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
        CommitLog logNode = commitLogService.formLogNode(Action.DELETE,
                mapper.tagToDto(requested),
                ALIAS_TAGS);
        commitLogService.writeAction(logNode);
    }

    @Override
    public TagDto update(TagDto dto) {
        TagDto updated = repository.findById(dto.getId())
                .map(found -> mapper.tagToDto(repository.save(mapper.dtoToTag(dto))))
                .orElseThrow(NotFoundException::new);
        CommitLog logNode = commitLogService.formLogNode(Action.UPDATE,
                updated,
                ALIAS_TAGS);
        commitLogService.writeAction(logNode);
        return updated;
    }

    @Override
    public TagDto findByName(String name) {
        return repository.findByName(name)
                .map(mapper::tagToDto)
                .orElseThrow(NotFoundException::new);
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
