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

   String SEQ_NEXT_VAL = "SELECT nextval(:seq)";
   String SEQ_SET_VAL = "SELECT setval(:seq, :val)";
   String SEQ_CURR_VAL = "SELECT last_value FROM seq";
   String DBLINK_CONNECTION = "SELECT dblink_connect('commit_log','host=localhost port=5432 dbname=node8081 user=postgres password=Osamu_720290 options=-csearch_path=')";
   String SELECT_FROM_COMMIT_LOG = "SELECT * FROM dblink('commit_log', 'SELECT * FROM public.changes') AS t(action text, table_name text, json_object text, id bigint, curr_seq bigint)";
   String INSERT_INTO_COMMIT_LOG = "SELECT dblink_exec('insert into changes(action, table_name, json_object, curr_seq) values(:action, :table, :json, :seq);')";
   String PUT_FROM_COMMIT_LOG = "SELECT dblink_exec('update changes set action = :action, table_name = :table, json_object = :json, curr_seq = :seq where curr_seq = :seq);')";
   String UPDATE_NODE_STATUS = "UPDATE node_statuses SET down_from = :time, node_status = :status where node_title = :node";
   String GET_NODE_STATUS = "SELECT node_status from node_statuses where node_title = :node";
}
