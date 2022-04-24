package ru.clevertec.ecl.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;
import ru.clevertec.ecl.exception.crud.DeletionException;
import ru.clevertec.ecl.exception.crud.SavingException;
import ru.clevertec.ecl.exception.crud.UpdatingException;
import ru.clevertec.ecl.exception.crud.notfound.TagNotFoundException;
import ru.clevertec.ecl.mapper.TagMapper;
import ru.clevertec.ecl.repository.TagRepository;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.util.matcherhelper.TagMatcherBuilder;

import java.util.List;
import java.util.Optional;


@Service
public class TagServiceImpl
        extends AbstractService<TagDto, Tag, TagRepository>
        implements TagService {

    private TagMapper mapper;
    private TagMatcherBuilder matcherBuilder;

    public TagServiceImpl(TagRepository repository, TagMapper mapper, TagMatcherBuilder matcherBuilder) {
        super(repository);
        this.mapper = mapper;
        this.matcherBuilder = matcherBuilder;
    }

    @Override
    public TagDto save(TagDto dto) {
        try {
            return mapper.tagToDto(repository.save(mapper.dtoToTag(dto)));
        } catch (DataIntegrityViolationException e) {
            throw new SavingException(e);
        }
    }

    @Override
    public TagDto findById(long id) {
        return repository.findById(id)
                .map(value -> mapper.tagToDto(value))
                .orElseThrow(() -> new TagNotFoundException(id + ""));
    }

    @Override
    public Page<TagDto> getAll(TagDto params, Pageable pageable) {
        List<String> requiredFilters = matcherBuilder.getRequiredFilters(params);
        if (!requiredFilters.isEmpty()) {
            ExampleMatcher example = matcherBuilder.buildMatcher(requiredFilters, params);
            return repository.findAll(Example.of(mapper.dtoToTag(params), example), pageable).map(mapper::tagToDto);
        } else {
            return repository.findAll(pageable).map(mapper::tagToDto);
        }
    }

    @Override
    public void delete(long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeletionException(new TagNotFoundException(id + ""));
        }
    }

    @Override
    public TagDto update(TagDto dto) {
        try {
            repository.findById(dto.getId())
                    .map(value -> mapper.tagToDto(value))
                    .orElseThrow(() -> new UpdatingException(new TagNotFoundException(dto.getId() + "")));
            return mapper.tagToDto(repository.save(mapper.dtoToTag(dto)));
        } catch (DataIntegrityViolationException e) {
            throw new UpdatingException(e);
        } catch (EmptyResultDataAccessException e) {
            throw new UpdatingException(new TagNotFoundException(dto.getId()+""));
        }
    }

    @Override
    public Optional<TagDto> findByName(String name) {
        return repository.findByName(name).map(mapper::tagToDto);
    }

    @Override
    @Transactional
    public TagDto getOrSave(TagDto tag) {
        Optional<TagDto> foundOrSaver = findByName(tag.getName());
        if (foundOrSaver.isPresent()) {
            return foundOrSaver.get();
        } else {
            try {
                return save(tag);
            } catch (DataIntegrityViolationException e) {
                throw new SavingException(e);
            }
        }
    }
}
