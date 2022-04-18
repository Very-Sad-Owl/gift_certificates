package ru.clevertec.ecl.repository;

import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Tag;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CommonRepository<Tag> {
    Optional<Tag> findByName(String name);
}
