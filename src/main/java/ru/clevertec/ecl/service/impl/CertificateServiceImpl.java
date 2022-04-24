package ru.clevertec.ecl.service.impl;


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
import ru.clevertec.ecl.util.matcherhelper.CertificateMatcherBuilder;
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
    private CertificateMatcherBuilder matcherBuilder;

    @Autowired
    public CertificateServiceImpl(CertificateRepository repository, CertificateMapper mapper, TagMapper tagMapper, TagServiceImpl tagServiceImpl, CertificateMatcherBuilder matcherBuilder) {
        super(repository);
        this.mapper = mapper;
        this.tagMapper = tagMapper;
        this.tagServiceImpl = tagServiceImpl;
        this.matcherBuilder = matcherBuilder;
    }

    public CertificateServiceImpl(CertificateRepository repository) {
        super(repository);
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
        Set<CertificateDto> page = new HashSet<>();
        List<String> requiredFilters = matcherBuilder.getRequiredFilters(filterCertificate);
        if (!requiredFilters.isEmpty()) {
            ExampleMatcher example = matcherBuilder.buildMatcher(requiredFilters, filterCertificate);
            page.addAll(repository.findAll(Example.of(mapper.dtoToCertificate(filterCertificate), example), pageable)
                    .map(mapper::certificateToDto).getContent());
        }
        if (filterCertificate.getFilteringTag() != null) {
            page.addAll(tagServiceImpl.findByName(filterCertificate.getFilteringTag())
                    .map(dto -> repository.findByTagsContains(tagMapper.dtoToTag(dto), pageable)
                            .map(mapper::certificateToDto))
                    .orElse(Page.empty()).getContent());
        }
        return new PageImpl<>(new ArrayList<>(page), pageable, page.size());
    }

    @Override
    public void delete(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeletionException(new CertificateNotFoundException(id+""));
        }
    }

    @Override
    public CertificateDto update(CertificateDto dto) {
        dto.setLastUpdateDate(LocalDateTime.now());
        return mapper.certificateToDto(repository.save(mapper.dtoToCertificate(dto)));
    }
}
