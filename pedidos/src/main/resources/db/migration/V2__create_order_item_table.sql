CREATE TABLE order_items (
  id bigserial NOT NULL,
  description varchar(255) DEFAULT NULL,
  quantity integer NOT NULL,
  order_id bigint NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (order_id) REFERENCES orders(id)
);