package ru.clevertec.ecl.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.CertificateParamsDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.mapper.CertificateMapper;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.CertificateRepository;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.service.CertificateService;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl
        extends AbstractService<CertificateDto, Certificate, CertificateRepository>
        implements CertificateService {

    private CertificateMapper mapper;
    private TagMapper tagMapper;
    private TagServiceImpl tagServiceImpl;

    @Autowired
    public CertificateServiceImpl(CertificateRepository repository, CertificateMapper mapper, TagMapper tagMapper, TagServiceImpl tagServiceImpl) {
        super(repository);
        this.mapper = mapper;
        this.tagMapper = tagMapper;
        this.tagServiceImpl = tagServiceImpl;
    }

    public CertificateServiceImpl(CertificateRepository repository) {
        super(repository);
    }

    @Override
    public CertificateDto save(CertificateDto e) {
        return null;
    }

    @Override
    public CertificateDto findById(long id) {
        Optional<Certificate> certificate = repository.findById(id);
        return certificate.map(value -> mapper.certificateToDto(value)).orElse(null);
    }

    @Override
    public Page<CertificateDto> getAll(CertificateParamsDto params, Pageable pageable) {
        Certificate updateCertificate = mapper.filterParamsToEntity(params);
        if (updateCertificate.getTags().isEmpty()) {
            ExampleMatcher example = ExampleMatcher.matchingAny()
                    .withMatcher("description", matcher -> matcher.contains().ignoreCase())
                    .withMatcher("name", matcher -> matcher.contains().ignoreCase());
            return repository.findAll(Example.of(updateCertificate, example), pageable)
                    .map(mapper::certificateToDto);
        } else {
//            Optional<TagDto> tag = tagServiceImpl.findByName(updateCertificate.getTags().stream().findFirst().get().getName());
            return new PageImpl<>(tagServiceImpl.findByName(updateCertificate.getTags().stream().findFirst().get().getName())
                    .map(dto -> repository.findByTagsContains(tagMapper.dtoToTag(dto)).stream()
                            .map(mapper::certificateToDto)
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList()));
        }

    }

    @Override
    public void delete(long id) {

    }

    @Override
    public CertificateDto put(CertificateDto dto) {
        return null;
    }

    @Override
    public CertificateDto patch(CertificateDto dto) {
        return null;
    }
}
