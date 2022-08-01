INSERT INTO node_status(
    node_title, last_updated, node_status, recommended_to_update_from)
VALUES ('node8070', '2011-12-03T10:15:30', 'true', '2011-12-03T10:15:30'),
       ('node8071', '2011-12-03T10:15:30', 'false', '2011-11-03T10:15:30'),
       ('node8072', '2011-12-03T10:15:30', 'true', '2011-12-03T10:15:30'),
       ('node8080', '2011-12-03T10:15:30', 'true', '2011-12-03T10:15:30'),
       ('node8081', '2011-12-03T10:15:30', 'true', '2011-12-03T10:15:30'),
       ('node8082', '2011-12-03T10:15:30', 'true', '2011-12-03T10:15:30'),
       ('node8090', '2011-12-03T10:15:30', 'false', '2011-11-03T10:15:30'),
       ('node8091', '2011-12-03T10:15:30', 'false', '2011-12-03T10:11:10'),
       ('node8092', '2011-12-03T10:15:30', 'true', '2011-12-03T10:15:30');

INSERT INTO commit_log(action, table_title, json_value, action_time, performed_on_node)
VALUES ('SAVE', 'tags', '{"id":1}', '2011-12-03T10:15:40', 8070),
       ('DELETE', 'tags','1', '2012-12-03T10:15:05', 8070),
       ('UPDATE', 'tags', '{"id":1}', '2013-12-03T10:15:14', 8070),
       ('UPDATE', 'orders', '{"id":2, surname: "xdd"}', '2013-12-03T10:15:15', 8070),
       ('UPDATE', 'orders', '{"id":1, surname: "xdd"}', '2013-12-03T10:15:15', 8090);