DROP TABLE IF EXISTS computer;
DROP TABLE IF EXISTS company;

CREATE TABLE company (
  company_id   BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  company_name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_company PRIMARY KEY (company_id)
);

CREATE TABLE computer (
  computer_id           BIGINT   NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  computer_name         VARCHAR(255),
  computer_introduced   DATETIME NULL,
  computer_discontinued DATETIME NULL,
  computer_company_id   BIGINT DEFAULT NULL,
  CONSTRAINT pk_computer PRIMARY KEY (computer_id),
  CONSTRAINT fk_company FOREIGN KEY (computer_company_id) REFERENCES company (company_id)
);

INSERT INTO company (company_id, company_name) VALUES (1, 'Company 1');
INSERT INTO company (company_id, company_name) VALUES (2, 'Company 2');
INSERT INTO company (company_id, company_name) VALUES (3, 'Company 3');

INSERT INTO computer (computer_id, computer_name, computer_introduced, computer_discontinued, computer_company_id)
VALUES (1, 'Computer 1', '0001-01-01', '0001-01-02', 1);
INSERT INTO computer (computer_id, computer_name, computer_introduced, computer_discontinued, computer_company_id)
VALUES (2, 'Computer 2', NULL, NULL, NULL);
INSERT INTO computer (computer_id, computer_name, computer_introduced, computer_discontinued, computer_company_id)
VALUES (3, 'Computer 3', NULL, '1994-09-12', 3);