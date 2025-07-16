INSERT INTO product (name, price, description, image_url)
VALUES ('테스트 상품1', 1000, '설명1', 'https://img1.url');
INSERT INTO product (name, price, description, image_url)
VALUES ('테스트 상품2', 2000, '설명2', 'https://img2.url');
INSERT INTO product (id, name, price, description, image_url)
VALUES (100, '테스트 상품3', 2000, '설명2', 'https://img2.url');
INSERT INTO product (id, name, price, description, image_url)
VALUES (101, '테스트 상품4', 2000, '설명2', 'https://img2.url');

INSERT INTO member (id, name)
VALUES (100, 'kim');
INSERT INTO member (id, name)
VALUES (101, 'park');

INSERT INTO member_auth (member_id, email, password, refresh_token)
VALUES (100, 'wjl0831@gmail.com', '$2a$10$C9fK7K1QZUXmCqZsMR8VdupxCBFe2g7kWyWeLb9bPP1WjAzBtHSSK',
        'abcd');
INSERT INTO member_auth (member_id, email, password, refresh_token)
VALUES (200, 'park@example.com', '$2a$10$7jxHkc3x2P8ZbmHcDxOXke95hWuHx6cLG3axrGgVqLEK3KLS/ZUQ6',
        'abcd');

INSERT INTO wish_item (member_id, product_id)
VALUES (100, 100);
INSERT INTO wish_item (member_id, product_id)
VALUES (100, 101);

