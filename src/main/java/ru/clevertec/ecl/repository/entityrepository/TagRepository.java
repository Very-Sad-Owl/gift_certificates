package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Tag;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static ru.clevertec.ecl.repository.UsedQuery.TOP_USER_MOST_COMMON_TAG;

@Repository
public interface TagRepository extends CommonRepository<Tag> {
    Optional<Tag> findByName(String name);
    Set<Tag> findByNameIn(Collection<String> name);
    @Query(
            value = TOP_USER_MOST_COMMON_TAG,
            nativeQuery = true)
    Optional<Tag> findTopUserMoreCommonTag();
}
