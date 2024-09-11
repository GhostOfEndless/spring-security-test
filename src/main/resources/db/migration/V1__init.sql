CREATE SCHEMA IF NOT EXISTS security;

CREATE TABLE security.t_app_users
(
    id           BIGSERIAL PRIMARY KEY,
    c_first_name VARCHAR(255),
    c_last_name  VARCHAR(255),
    c_email      VARCHAR(255),
    c_password   VARCHAR(255),
    c_role       VARCHAR(255)
);