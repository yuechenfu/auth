INSERT INTO account (id, username, password, personId,  extra, createAt, updateAt)
VALUES (1, 'testusername1@hiveel.com', '88206f249a9a734e9f0cc7a9f4b8d011', '1',  'DR', '2017-01-01', '2017-01-01');
INSERT INTO account (id, username, password, personId,  extra, createAt, updateAt)
VALUES (2, 'testusername2@hiveel.com', '88206f249a9a734e9f0cc7a9f4b8d011', '2',   'VE', '2017-01-01', '2017-01-01');
INSERT INTO account (id, username, password, personId,  extra, createAt, updateAt)
VALUES (3, 'testusername3@hiveel.com', '88206f249a9a734e9f0cc7a9f4b8d011', '3',  'AS', '2017-01-01', '2017-01-01');
INSERT INTO account (id, username, password, personId,  extra, createAt, updateAt)
VALUES (5, 'testusername5@hiveel.com', '88206f249a9a734e9f0cc7a9f4b8d011', '5',   'VE', '2017-01-01', '2017-01-01');
INSERT INTO account (id, username, password, personId,  extra, createAt, updateAt)
VALUES (6, 'testusername6@hiveel.com', '88206f249a9a734e9f0cc7a9f4b8d011', '6',   'DR', '2017-01-01', '2017-01-01');

INSERT INTO loginRecord (id, personId, updateAt, ip, device)
VALUES (1, 1, '2019-03-04T12:34:21', '140.115.61.93', 'device');
INSERT INTO loginRecord (id, personId, updateAt, ip, device)
VALUES (2, 1, '2019-02-05T12:34:21', '140.115.61.93', 'device');
INSERT INTO loginRecord (id, personId, updateAt, ip, device)
VALUES (3, 1, '2019-02-07T12:34:21', '140.115.61.93', 'device');
INSERT INTO loginRecord (id, personId, updateAt, ip, device)
VALUES (4, 2, '2019-02-08T12:34:21', '140.115.61.93', 'device');
INSERT INTO loginRecord (id, personId, updateAt, ip, device)
VALUES (5, 5, '2019-03-05T12:34:21', '140.115.61.93', 'device');
INSERT INTO loginRecord (id, personId, updateAt, ip, device)
VALUES (6, 5, '2019-03-07T12:34:21', '140.115.61.93', 'device');


INSERT INTO forget (`id`, `accountId`,  `code`,  `createAt`,  `updateAt`)
VALUES (5, 1,  '531245',  '2019-02-28T02:20:20',  '2019-02-28T02:20:20');
INSERT INTO forget (`id`, `accountId`,  `code`,  `createAt`,  `updateAt`)
VALUES (6, 1,  '531245',  '2019-02-28T03:20:20',  '2019-02-28T02:20:20');
INSERT INTO forget (`id`, `accountId`,  `code`,  `createAt`,  `updateAt`)
VALUES (7, 1,  '531245',  '2019-02-28T04:20:20',  '2019-02-28T02:20:20');
INSERT INTO forget (`id`, `accountId`,  `code`,  `createAt`,  `updateAt`)
VALUES (8, 2,  '531245',  '2019-02-18T02:20:20',  '2019-02-28T02:20:20');
INSERT INTO forget (`id`, `accountId`,  `code`,  `createAt`,  `updateAt`)
VALUES (9, 2,  '531245',  '2019-02-18T02:20:20',  '2019-02-28T02:20:20');
INSERT INTO forget (`id`, `accountId`,  `code`,  `createAt`,  `updateAt`)
VALUES (10, 2,  '531245',  '2019-02-18T02:20:20',  '2019-02-28T02:20:20');
