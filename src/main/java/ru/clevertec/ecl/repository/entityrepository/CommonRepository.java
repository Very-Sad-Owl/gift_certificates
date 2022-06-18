package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ru.clevertec.ecl.entity.AbstractEntity;

import static ru.clevertec.ecl.repository.UsedQuery.*;

@NoRepositoryBean
public interface CommonRepository<E extends AbstractEntity> extends JpaRepository<E, Long> {
    @Query(value = SEQ_NEXT_VAL, nativeQuery = true)
    long getSeqNextVal(@Param("seq") String seq);
    @Query(value = SEQ_SET_VAL, nativeQuery = true)
    void setSeqVal(@Param("seq") String seq, @Param("val") long val);
//    @Query(value = SEQ_CURR_VAL, nativeQuery = true)
//    long currSeqVal(@Param("seq") String seq);
    @Query(value = SEQ_CURR_VAL, nativeQuery = true)
    long currSeqVal();
}
