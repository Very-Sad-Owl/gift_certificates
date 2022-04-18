package ru.clevertec.ecl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.entity.AbstractEntity;
import ru.clevertec.ecl.repository.CommonRepository;
import ru.clevertec.ecl.service.CRUDService;

@RequiredArgsConstructor
public abstract class AbstractService<
        E extends AbstractModel,
        T extends AbstractEntity,
        R extends CommonRepository<T>>
        implements CRUDService<E> {

    protected final R repository;
}
