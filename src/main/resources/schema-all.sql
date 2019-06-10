DROP TABLE workflow_instances IF EXISTS;
DROP TABLE workflows IF EXISTS;
DROP TABLE employees IF EXISTS;
DROP TABLE parsing_errors IF EXISTS;
DROP TABLE business_errors IF EXISTS;

CREATE TABLE employees (
  id         BIGINT IDENTITY NOT NULL PRIMARY KEY,
  code       VARCHAR(50) UNIQUE  NOT NULL,
  full_name  VARCHAR(200)        NOT NULL,
  email      VARCHAR(100) UNIQUE NOT NULL,
  contractor BOOLEAN             NOT NULL
);

CREATE TABLE workflows (
  id        BIGINT IDENTITY NOT NULL PRIMARY KEY,
  code      VARCHAR(50) UNIQUE NOT NULL,
  name      VARCHAR(200)       NOT NULL,
  author_id BIGINT             NOT NULL REFERENCES employees (id),
  version   BIGINT             NOT NULL
);

CREATE TABLE workflow_instances (
  id          BIGINT IDENTITY NOT NULL PRIMARY KEY,
  code        VARCHAR(100) UNIQUE NOT NULL,
  workflow_id BIGINT              NOT NULL REFERENCES workflows (id),
  assignee_id BIGINT              NOT NULL REFERENCES employees (id),
  step        VARCHAR(100)        NOT NULL,
  status      VARCHAR(20)         NOT NULL
);

CREATE TABLE parsing_errors (
  id          BIGINT IDENTITY NOT NULL PRIMARY KEY,
  resource    VARCHAR(100)  NOT NULL,
  error       VARCHAR(30)  NOT NULL,
  line_number BIGINT       NOT NULL,
  line        VARCHAR(400) NOT NULL
);

CREATE TABLE business_errors (
  id                   BIGINT IDENTITY NOT NULL PRIMARY KEY,
  entity               VARCHAR(30)  NOT NULL,
  record_start_line    BIGINT       NOT NULL,
  record_end_line      BIGINT       NOT NULL,
  constraint_violation VARCHAR(400) NOT NULL
);
