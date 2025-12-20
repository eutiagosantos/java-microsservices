CREATE TABLE orders (
  id bigserial NOT NULL,
  date_time timestamp NOT NULL,
  status varchar(255) NOT NULL,
  PRIMARY KEY (id)
);