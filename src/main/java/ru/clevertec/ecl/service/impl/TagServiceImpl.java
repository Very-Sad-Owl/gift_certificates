package ru.clevertec.ecl.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.clevertec.ecl.dto.CertificateParamsDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.TagRepository;
import ru.clevertec.ecl.service.TagService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TagServiceImpl
        extends AbstractService<TagDto, Tag, TagRepository>
        implements TagService {

    TagMapper mapper;

    public TagServiceImpl(TagRepository repository, TagMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    public TagDto save(TagDto e) {
        return null;
    }

    @Override
    public TagDto findById(long id) {
        return null;
    }

    @Override
    public Page<TagDto> getAll(CertificateParamsDto params, Pageable pageable) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public TagDto put(TagDto dto) {
        return null;
    }

    @Override
    public TagDto patch(TagDto dto) {
        return null;
    }

    @Override
    public Optional<TagDto> findByName(String name) {
        return repository.findByName(name).map(mapper::tagToDto);
    }
}
