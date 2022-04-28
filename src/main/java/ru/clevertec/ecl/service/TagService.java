package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagService extends CRUDService<TagDto> {
    Optional<TagDto> findByName(String name);
    TagDto getOrSave(TagDto tag);
    Set<TagDto> findByNames(Collection<String> names);
}
