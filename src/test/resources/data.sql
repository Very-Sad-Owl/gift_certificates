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

INSERT INTO commit_log(action, table_title, json_value, action_time, performed_on_node, entity_id)
VALUES ('SAVE', 'tags', '{"id":1}', '2011-12-03T10:15:40', 8070, 1),
       ('DELETE', 'tags','{"id":1}', '2012-12-03T10:15:05', 8072, 1),
       ('SAVE', 'tags', '{"id":2}', '2013-12-03T10:15:14', 8071, 2),
       ('SAVE', 'orders', '{"id":1, surname: "xdd"}', '2013-12-03T10:15:15', 8082, 1),
       ('SAVE', 'orders', '{"id":2, surname: "xdd2"}', '2013-12-03T10:15:15', 8082, 2),
       ('SAVE', 'orders', '{"id":3, surname: "xdd3"}', '2013-12-03T10:15:15', 8080, 3),
       ('UPDATE', 'orders', '{"id":1, surname: "xd"}', '2013-12-03T10:15:15', 8080, 1),
       ('UPDATE', 'orders', '{"id":2, surname: "xdd"}', '2013-12-03T10:15:15', 8081, 2),
       ('DELETE', 'orders', '{"id":3, surname: "xdd33"}', '2013-12-03T10:15:15', 8082, 3);