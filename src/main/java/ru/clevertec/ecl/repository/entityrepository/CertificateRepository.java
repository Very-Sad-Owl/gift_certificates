package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.Tag;

import java.util.Set;

@Repository
public interface CertificateRepository extends CommonRepository<Certificate> {
    Page<Certificate> findByTagsContains(Tag tag, Pageable pageable);
    Page<Certificate> findByTagsIn(Set<Tag> tags, Pageable pageable);
}
