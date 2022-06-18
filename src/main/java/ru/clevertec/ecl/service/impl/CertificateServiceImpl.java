package ru.clevertec.ecl.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.entityrepository.CertificateRepository;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.service.CertificateService;
import ru.clevertec.ecl.util.matcherhelper.MatcherBuilder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl
        extends AbstractService<CertificateDto, Certificate, CertificateRepository>
        implements CertificateService {

    private CertificateMapper mapper;
    private TagMapper tagMapper;
    private TagServiceImpl tagServiceImpl;

    @Autowired
    public CertificateServiceImpl(CertificateRepository repository, MatcherBuilder<CertificateDto> filter, CertificateMapper mapper, TagMapper tagMapper, TagServiceImpl tagServiceImpl) {
        super(repository, filter);
        this.mapper = mapper;
        this.tagMapper = tagMapper;
        this.tagServiceImpl = tagServiceImpl;
    }

    public CertificateServiceImpl(CertificateRepository repository, MatcherBuilder<CertificateDto> filter) {
        super(repository, filter);
    }

    @Transactional
    @Override
    public CertificateDto save(CertificateDto dto) {
        for (TagDto el : dto.getTags()) {
            el.setId(tagServiceImpl.getOrSaveIfExists(el).getId());
        }
        dto.setCreateDate(LocalDateTime.now());
        dto.setLastUpdateDate(LocalDateTime.now());
        Certificate certificate = repository.save(mapper.dtoToCertificate(dto));
        return mapper.certificateToDto(certificate);
    }

    @Override
    public CertificateDto findById(long id) {
        return repository.findById(id)
                .map(value -> mapper.certificateToDto(value))
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<CertificateDto> getAll(CertificateDto filterCertificate, Pageable pageable) {
        boolean areAmyFiltersApplied = false;
        ExampleMatcher xd = filterMatcher.buildMatcher(filterCertificate);
        Set<CertificateDto> page = new HashSet<>();
        if (!filterMatcher.isEmpty(filterCertificate)) {
            page.addAll(repository.findAll(Example.of(mapper.dtoToCertificate(filterCertificate),
                            filterMatcher.buildMatcher(filterCertificate)), pageable)
                    .map(mapper::certificateToDto).getContent());
            areAmyFiltersApplied = true;
        }
        if (filterCertificate.getFilteringTags() != null) {
            Set<Tag> tags = tagServiceImpl.findByNames(filterCertificate.getFilteringTags())
                    .stream()
                    .map(tagMapper::dtoToTag)
                    .collect(Collectors.toSet());
            page.addAll(repository.findByTagsIn(tags, pageable).map(mapper::certificateToDto).getContent());
            areAmyFiltersApplied = true;
        }
        return areAmyFiltersApplied
                ? new PageImpl<>(new ArrayList<>(page), pageable, page.size())
                : new PageImpl<>(repository.findAll()).map(mapper::certificateToDto); //TODO: to test
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public CertificateDto update(CertificateDto dto) {
        dto.setLastUpdateDate(LocalDateTime.now());
        return mapper.certificateToDto(repository.save(mapper.dtoToCertificate(dto)));
    }

    @Override
    public long getSequenceNextVal() {
        return repository.getSeqNextVal("node"+currentPort);
    }

    @Override
    public void updateSequence(long val) {
        repository.setSeqVal("node"+currentPort, val);
    }

    @Override
    public long getSequenceCurrVal() {
        return repository.currSeqVal();
    }
}
