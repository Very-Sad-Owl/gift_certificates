package ru.clevertec.ecl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Tag;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends CommonRepository<Tag> {
    Optional<Tag> findByName(String name);
    Set<Tag> findByNameIn(Collection<String> name);
}
