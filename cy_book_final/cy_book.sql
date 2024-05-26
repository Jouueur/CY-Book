
CREATE DATABASE IF NOT EXISTS cy_book;

USE cy_book;

CREATE TABLE IF NOT EXISTS customer (
  id_customer int NOT NULL AUTO_INCREMENT,
  name_customer varchar(45) NOT NULL,
  last_name_customer varchar(45) NOT NULL,
  email_customer varchar(45) NOT NULL,
  PRIMARY KEY (id_customer),
  UNIQUE KEY id_customer_UNIQUE (id_customer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS borrowing (
  id_borrowing int NOT NULL AUTO_INCREMENT,
  id_customer int NOT NULL,
  isbn varchar(40) NOT NULL,
  start_date date NOT NULL,
  end_date date NOT NULL,
  borrowed boolean NOT NULL DEFAULT false, 
  PRIMARY KEY (id_borrowing),
  CONSTRAINT borrowing_ibfk_1 FOREIGN KEY (id_customer) REFERENCES customer (id_customer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;