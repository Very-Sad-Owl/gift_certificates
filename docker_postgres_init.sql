CREATE DATABASE node8070
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE node8080
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE DATABASE node8090
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

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

INSERT INTO users(id, name, surname)
VALUES (1, 'Oleg', 'Olegov'),
       (2, 'Olga', 'Mailychko')
ON CONFLICT (id) DO NOTHING;
SELECT setval('seq_user', 2);

INSERT INTO gift_certificate
(id, name, description, price, duration, createdate, lastupdatedate)
VALUES (0, 'deleted', 'none', 0, 999, '2011-12-03T10:15:40', '2011-12-03T10:15:40')
ON CONFLICT (id) DO NOTHING;