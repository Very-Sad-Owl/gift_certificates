package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService extends CRUDService<TagDto> {
    Optional<TagDto> findByName(String name);
}
