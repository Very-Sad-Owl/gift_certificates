package ru.clevertec.ecl.repository;

public interface Query {
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

}
