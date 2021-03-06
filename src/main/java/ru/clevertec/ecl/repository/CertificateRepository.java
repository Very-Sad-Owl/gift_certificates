package ru.clevertec.ecl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.Tag;

import java.util.Collection;
import java.util.Set;

@Repository
public interface CertificateRepository extends CommonRepository<Certificate> {
    Page<Certificate> findByTagsContains(Tag tag, Pageable pageable);
    Page<Certificate> findByTagsIn(Set<Tag> tags, Pageable pageable);

//    @Query(value = "select * from gift_certificate " +
//            "where POSITION(%:description% in gift_certificate.description) != 0 ", nativeQuery = true)
//    List<Certificate> findAllByDescription(@Param("description") String description);
//
//    @Query(value = "select * from gift_certificate " +
//            "where POSITION(%:name% in gift_certificate.name) != 0 ", nativeQuery = true)
//    List<Certificate> findAllByName(@Param("name") String name);
}
