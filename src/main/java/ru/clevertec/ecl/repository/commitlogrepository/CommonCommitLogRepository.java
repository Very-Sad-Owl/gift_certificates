package ru.clevertec.ecl.repository.commitlogrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import ru.clevertec.ecl.entity.commitlogentities.AbstractEntity;

/**
 * Abstraction on all custom repositories.
 *
 * See also  {@link NoRepositoryBean}
 *
 * @author Olga Mailychko
 *
 */
@NoRepositoryBean
public interface CommonCommitLogRepository<E extends AbstractEntity> extends JpaRepository<E, Long> {
}
