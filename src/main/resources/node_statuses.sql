INSERT INTO node_status(
    nodetitle, lastupdated, nodestatus, recommendedtoupdatefrom)
VALUES ('node8070', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8071', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8072', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8080', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8081', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8082', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8090', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8091', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP),
       ('node8092', CURRENT_TIMESTAMP, 'false', CURRENT_TIMESTAMP)
ON CONFLICT (nodetitle) DO NOTHING;