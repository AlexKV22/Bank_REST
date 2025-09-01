INSERT INTO users VALUES (1, 'Egor', '$2a$12$.BekmzUU/kkoQXSafpIJw..XjXORfhhLwRaXVOuoSByc4HzWJlCom'),
                          (2, 'Masha', '$2a$10$DrMcxpMk9kQ7VR9zTR981OpX.JM8J08NfTcq01L.AdhpvelEooE6y');

INSERT INTO cards VALUES (1, '3333332333456221234', 1, '2052.07.20', 4462.00, 'ACTIVE'),
                         (2, '3333332333456229934', 2, '2052.07.20', 4462.00, 'ACTIVE');

INSERT INTO roles VALUES (1, 'ADMIN'),
                         (2, 'USER');



INSERT INTO user_roles VALUES (1, 1),
                              (2,2);
