package ru.clevertec.ecl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.ecl.entity.Certificate;
import ru.clevertec.ecl.entity.Tag;

import java.util.List;

@Repository
public interface CertificateRepository extends CommonRepository<Certificate> {
    List<Certificate> findByTagsContains(Tag tag);

//    @Query(value = "select * from gift_certificate " +
//            "where POSITION(%:description% in gift_certificate.description) != 0 ", nativeQuery = true)
//    List<Certificate> findAllByDescription(@Param("description") String description);
//
//    @Query(value = "select * from gift_certificate " +
//            "where POSITION(%:name% in gift_certificate.name) != 0 ", nativeQuery = true)
//    List<Certificate> findAllByName(@Param("name") String name);
}
