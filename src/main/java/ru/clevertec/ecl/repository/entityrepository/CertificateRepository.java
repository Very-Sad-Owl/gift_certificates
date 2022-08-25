package ru.clevertec.ecl.repository.entityrepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.baseentities.Certificate;
import ru.clevertec.ecl.entity.baseentities.Tag;
import ru.clevertec.ecl.entity.commitlogentities.CommitLog;

import java.util.Set;

import static ru.clevertec.ecl.repository.UsedQuery.CERTIFICATE_SEQ_CURR_VAL;
import static ru.clevertec.ecl.repository.UsedQuery.GET_CERTIFICATES_WITH_TAGS;

/**
 * Repository for {@link Certificate} entity.
 *
 * @author Olga Mailychko
 *
 */
@Repository
public interface CertificateRepository extends CommonRepository<Certificate> {
    Page<Certificate> findByTagsContains(Tag tag, Pageable pageable);
    Page<Certificate> findAllByNameContaining(String name, Pageable pageable);
    Page<Certificate> findAllByDescriptionContaining(String description, Pageable pageable);
    Page<Certificate> findAllByDescriptionContainingAndNameContaining(String description, String name, Pageable pageable);
    Set<Certificate> findAllByTagsContains(Tag tag);
    @Query(nativeQuery = true,value = GET_CERTIFICATES_WITH_TAGS)
    Set<Certificate> findAllByTagsIn(@Param("tags") Set<String> tags, @Param("border") int border);
    @Query(value = CERTIFICATE_SEQ_CURR_VAL, nativeQuery = true)
    long getCurrentCertificateSequence();
}
