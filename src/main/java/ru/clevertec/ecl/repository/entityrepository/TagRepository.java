package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static ru.clevertec.ecl.repository.UsedQuery.TAG_SEQ_CURR_VAL;
import static ru.clevertec.ecl.repository.UsedQuery.TOP_USER_MOST_COMMON_TAG;

/**
 * Repository for {@link Tag} entity.
 *
 * @author Olga Mailychko
 *
 */
@Repository
public interface TagRepository extends CommonRepository<Tag> {
    Optional<Tag> findByName(String name);
    Set<Tag> findByNameIn(Collection<String> name);
    @Query(
            value = TOP_USER_MOST_COMMON_TAG,
            nativeQuery = true)
    Optional<Tag> findTopUserMoreCommonTag();
    @Query(value = TAG_SEQ_CURR_VAL, nativeQuery = true)
    long getCurrentTagSequence();
}
