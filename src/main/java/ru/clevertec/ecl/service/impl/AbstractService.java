package ru.clevertec.ecl.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.entity.AbstractEntity;
import ru.clevertec.ecl.repository.CommonRepository;
import ru.clevertec.ecl.service.CRUDService;
import ru.clevertec.ecl.util.matcherhelper.MatcherBuilder;

@RequiredArgsConstructor
public abstract class AbstractService<
        E extends AbstractModel,
        T extends AbstractEntity,
        R extends CommonRepository<T>>
        implements CRUDService<E> {

    protected final R repository;
    protected final MatcherBuilder<E> filterMatcher;

}
