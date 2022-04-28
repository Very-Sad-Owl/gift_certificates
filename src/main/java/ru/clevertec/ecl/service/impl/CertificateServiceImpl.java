package ru.clevertec.ecl.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;
import ru.clevertec.ecl.exception.crud.DeletionException;
import ru.clevertec.ecl.exception.crud.notfound.CertificateNotFoundException;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.CertificateRepository;
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
    public CertificateDto save(CertificateDto e) {
        Set<TagDto> tags = new HashSet<>();
        for (TagDto el : e.getTags()) {
//            TagDto tag = tagServiceImpl.getOrSave(el);
//            tag.getCertificates().add(e);
//            tags.add(tag);
            el = tagServiceImpl.getOrSave(el);
//            if (el.getCertificates() != null)
//                el.getCertificates().add(e);
//            else
//                el.setCertificates(new HashSet<>());
        }
//        for (TagDto el : e.getTags()) {
////            tags.add(tagServiceImpl.getOrSave(el));
//            if (el.getCertificates() != null)
//                el.getCertificates().add(e);
//            else el.setCertificates(new HashSet<>());
//        }
        e.setCreateDate(LocalDateTime.now());
        e.setLastUpdateDate(LocalDateTime.now());
        Certificate certificate = repository.save(mapper.dtoToCertificate(e));
        CertificateDto dto = mapper.certificateToDto(certificate);
//        dto.setTags(tags);
        return dto;
    }

    @Override
    public CertificateDto findById(long id) {
        return repository.findById(id)
                .map(value -> mapper.certificateToDto(value))
                .orElseThrow(() -> new CertificateNotFoundException(id + ""));
    }

    @Override
    public Page<CertificateDto> getAll(CertificateDto filterCertificate, Pageable pageable) {
        boolean areAmyFiltersApplied = false;
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
//            page.addAll(tagServiceImpl.findByName(filterCertificate.getFilteringTag())
//                    .map(dto -> repository.findByTagsContains(tagMapper.dtoToTag(dto), pageable)
//                            .map(mapper::certificateToDto))
//                    .orElse(Page.empty()).getContent());
        }
        return areAmyFiltersApplied
                ? new PageImpl<>(new ArrayList<>(page), pageable, page.size())
                : new PageImpl<>(repository.findAll()).map(mapper::certificateToDto);
    }

    @Override
    public void delete(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeletionException(new CertificateNotFoundException(id + ""));
        }
    }

    @Override
    public CertificateDto update(CertificateDto dto) {
        dto.setLastUpdateDate(LocalDateTime.now());
        return mapper.certificateToDto(repository.save(mapper.dtoToCertificate(dto)));
    }
}
