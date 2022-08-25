package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ru.clevertec.ecl.entity.baseentities.AbstractEntity;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;

import static ru.clevertec.ecl.repository.UsedQuery.*;

/**
 * Abstraction for custom repository entities.
 *
 * See also  {@link NoRepositoryBean}
 *
 * @author Olga Mailychko
 *
 */
@NoRepositoryBean
public interface CommonRepository<E extends AbstractEntity> extends JpaRepository<E, Long> {
    @Query(value = SEQ_NEXT_VAL, nativeQuery = true)
    long getSeqNextVal(@Param("sequence") String seq);
    @Query(value = SEQ_SET_VAL, nativeQuery = true)
    void setSeqVal(@Param("sequence") String seq, @Param("val") long val);
}
