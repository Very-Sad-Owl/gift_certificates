package ru.clevertec.ecl.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;
import ru.clevertec.ecl.exception.NotFoundException;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.TagRepository;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.util.matcherhelper.MatcherBuilder;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class TagServiceImpl
        extends AbstractService<TagDto, Tag, TagRepository>
        implements TagService {

    private TagMapper mapper;

    public TagServiceImpl(TagRepository repository, TagMapper mapper, MatcherBuilder<TagDto> matcherBuilder) {
        super(repository, matcherBuilder);
        this.mapper = mapper;
    }

    @Override
    public TagDto save(TagDto dto) {
        return mapper.tagToDto(repository.save(mapper.dtoToTag(dto)));
    }

    @Override
    public TagDto findById(long id) {
        return repository.findById(id)
                .map(value -> mapper.tagToDto(value))
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Set<TagDto> findByNames(Collection<String> names) {
        return repository.findByNameIn(names).stream()
                .map(mapper::tagToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Page<TagDto> getAll(TagDto params, Pageable pageable) {
        return repository.findAll(pageable).map(mapper::tagToDto);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public TagDto update(TagDto dto) {
        return repository.findById(dto.getId())
                .map(found -> mapper.tagToDto(repository.save(mapper.dtoToTag(dto))))
                .orElseThrow(NotFoundException::new);

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
        return repository.findByName(tag.getName())
                .map(mapper::tagToDto)
                .orElseGet(() ->
                        mapper.tagToDto(repository.save(mapper.dtoToTag(tag))))
                ;
    }

    @Override
    public TagDto findTopUserMoreCommonTag() {
        return repository.findTopUserMoreCommonTag()
                .map(mapper::tagToDto)
                .orElseThrow(NotFoundException::new);
    }
}
