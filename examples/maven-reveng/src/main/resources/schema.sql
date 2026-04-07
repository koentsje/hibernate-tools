-- ==========================================================================
-- Sample schema for Hibernate Tools reverse engineering example
-- ==========================================================================
--
-- Three tables with relationships:
--   PERSON  1---*  ITEM     (one-to-many via OWNER_ID foreign key)
--   PERSON  1---1  ADDRESS  (one-to-one via unique PERSON_ID foreign key)
--

CREATE TABLE IF NOT EXISTS PERSON (
    ID          BIGINT       NOT NULL AUTO_INCREMENT,
    FIRST_NAME  VARCHAR(100) NOT NULL,
    LAST_NAME   VARCHAR(100) NOT NULL,
    EMAIL       VARCHAR(255),
    BIRTH_DATE  DATE,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS ADDRESS (
    ID          BIGINT       NOT NULL AUTO_INCREMENT,
    STREET      VARCHAR(200) NOT NULL,
    CITY        VARCHAR(100) NOT NULL,
    ZIP_CODE    VARCHAR(20),
    COUNTRY     VARCHAR(100) NOT NULL,
    PERSON_ID   BIGINT       NOT NULL UNIQUE,
    PRIMARY KEY (ID),
    FOREIGN KEY (PERSON_ID) REFERENCES PERSON(ID)
);

CREATE TABLE IF NOT EXISTS ITEM (
    ID          BIGINT       NOT NULL AUTO_INCREMENT,
    NAME        VARCHAR(200) NOT NULL,
    DESCRIPTION VARCHAR(1000),
    PRICE       DECIMAL(10,2),
    OWNER_ID    BIGINT       NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (OWNER_ID) REFERENCES PERSON(ID)
);
