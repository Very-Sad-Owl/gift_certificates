package ru.clevertec.ecl.repository;

public interface UsedQuery {
   String TOP_USER_MOST_COMMON_TAG = "select * from tag " +
           "where id = " +
           "(" +
                 "select xd.tag_id from " +
                 "( " +
                    "select certificate_tag.tag_id from orders " +
                    "join ( " +
                    "select user_id usr, sum(price) total from orders " +
                    "group by user_id " +
                    "order by total desc " +
                    "limit 1 " +
                 ") sub " +
                 "on user_id = sub.usr " +
                 "join certificate_tag " +
                 "on orders.certificate_id = certificate_tag.certificate_id " +
                 ") xd " +
              "group by xd.tag_id " +
              "order by count(xd.tag_id) desc " +
              "limit 1 " +
           ") ";

   String SEQ_NEXT_VAL = "SELECT nextval(:sequence)";
   String SEQ_SET_VAL = "SELECT setval(:sequence, :val)";
   String ORDER_SEQ_CURR_VAL = "SELECT last_value FROM seq";
   String TAG_SEQ_CURR_VAL = "SELECT last_value FROM seq_tag";
   String CERTIFICATE_SEQ_CURR_VAL = "SELECT last_value FROM seq_cert";
   String GET_CERTIFICATES_WITH_TAGS = "select " +
           "gift_certificate.id, gift_certificate.create_date, " +
           "gift_certificate.description, gift_certificate.duration, " +
           "gift_certificate.last_update_date, " +
           "gift_certificate.name, gift_certificate.price " +
           "from gift_certificate " +
           "join certificate_tag " +
           "on gift_certificate.id = certificate_tag.certificate_id " +
           "join tag " +
           "on tag.id = certificate_tag.tag_id " +
           "where tag.name in (:tags) " +
           "limit :border";
}
