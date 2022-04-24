package ru.clevertec.ecl.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.ecl.dto.CertificateParamsDto;
import ru.clevertec.ecl.dto.AbstractModel;

import java.util.List;

public interface CRUDService<T extends AbstractModel> {
    T save(T e);
    T findById(long id);
    Page<T> getAll(T params, Pageable pageable);
    void delete(long id);
    T update(T dto);
}
