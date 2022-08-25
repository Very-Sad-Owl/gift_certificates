INSERT INTO users(id, name, surname)
VALUES (1, 'Oleg', 'Olegov'),
       (2, 'Olga', 'Mailychko')
ON CONFLICT (id) DO NOTHING;
SELECT setval('seq_user', 2);

INSERT INTO gift_certificate
(id, name, description, price, duration, createdate, lastupdatedate)
VALUES (0, 'deleted', 'none', 0, 999, '2011-12-03T10:15:40', '2011-12-03T10:15:40')
ON CONFLICT (id) DO NOTHING;