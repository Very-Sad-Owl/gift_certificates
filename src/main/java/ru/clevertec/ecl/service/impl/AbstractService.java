package ru.clevertec.ecl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.baseentities.User;
import ru.clevertec.ecl.interceptor.common.ClusterProperties;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.entity.baseentities.AbstractEntity;
import ru.clevertec.ecl.repository.entityrepository.CommonRepository;
import ru.clevertec.ecl.service.CRUDService;

/**
 * Abstract service class. Provides {@link ClusterProperties} and {@link CommonRepository}
 * objects for all its subclasses.
 * Parametrizes with any {@link AbstractModel}, {@link AbstractEntity} and {@link CommonRepository}
 * implementations.
 *
 * See also {@link CRUDService}
 *
 * @author Olga Mailychko
 *
 */
@RequiredArgsConstructor
public abstract class AbstractService<
        E extends AbstractModel,
        T extends AbstractEntity,
        R extends CommonRepository<T>>
        implements CRUDService<E> {

    /**
     * Properties of whole cluster
     */
    protected final ClusterProperties clusterProperties;
    /**
     * DAO
     */
    protected final R repository;

}
