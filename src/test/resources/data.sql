INSERT INTO tag(name) VALUES ('birthday'), ('christmas'), ('holiday');

INSERT INTO gift_certificate
(name, description, price, duration, create_date, last_update_date)
VALUES ('hpb', 'happy birthday', 2.1, 60, CURRENT_DATE, CURRENT_DATE ),
       ('mc', 'merry christmas', 5.6, 10, CURRENT_DATE, CURRENT_DATE ),
       ('may 1', 'may the first', 10.5, 1, CURRENT_DATE, CURRENT_DATE );

INSERT INTO certificate_tag(
    certificate_id, tag_id)
VALUES (1, 1), (2, 2), (2, 3), (3, 3);