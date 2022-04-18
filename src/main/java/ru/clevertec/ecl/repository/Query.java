package ru.clevertec.ecl.repository;

public interface Query {
   String GET_CERTIFICATES = "select distinct certificate_tag.certificate_id, gift_certificate.*, tag.name tag_name " +
           "from certificate_tag join gift_certificate " +
           "on certificate_tag.certificate_id = gift_certificate.id " +
           "join tag " +
           "on certificate_tag.tag_id = tag.id";
   String GET_CERTIFICATE_TAGS = "select tag.id, tag.name from certificate_tag " +
           "join tag " +
           "on certificate_tag.tag_id = tag.id " +
           "where certificate_tag.certificate_id = :id";

   String GET_CERTIFICATES_WITH_TAGS = "select * from full_info where certificate_id in " +
           "(" +
           " select distinct certificate_id from full_info " +
           " {where}" +
           ")" +
           "{order}" +
           "{limit} {offset}";

   //parts

}
