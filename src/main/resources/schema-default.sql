CREATE TABLE product
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    price       INT          NOT NULL,
    description TEXT         NOT NULL,
    image_url   VARCHAR(512) NOT NULL
);

CREATE TABLE member
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE member_auth
(
    member_id     BIGINT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(512)
);

CREATE TABLE wish_item
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT,
    product_id BIGINT,
    CONSTRAINT uq_member_product UNIQUE (member_id, product_id),
    CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id)
);