CREATE TABLE tb_payments (
 id SERIAL PRIMARY KEY,
 value decimal(19,2) NOT NULL,
 name varchar(100) DEFAULT NULL,
 number varchar(19) DEFAULT NULL,
 expiration varchar(7) DEFAULT NULL,
 code varchar(3) DEFAULT NULL,
 status varchar(255) NOT NULL,
 payment_form_id bigint NOT NULL,
 order_id bigint NOT NULL,
 create_at date DEFAULT NULL,
 remove_at date DEFAULT NULL
);