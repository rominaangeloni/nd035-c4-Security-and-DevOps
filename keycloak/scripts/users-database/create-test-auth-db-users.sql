create database IF NOT EXISTS auth;
use auth;

create table IF NOT EXISTS user_credentials
(
    user_id       bigint                             not null
        primary key,
    email         varchar(150)                       not null,
    password      varchar(75)                        not null,
    status_id     int                                not null,
    status        varchar(15)                        null,
    create_date   datetime default CURRENT_TIMESTAMP not null,
    modified_date datetime default CURRENT_TIMESTAMP null,
    constraint UQ_user_credentials_username
        unique (email)
);

INSERT INTO user_credentials (user_id, email, password,status_id) VALUES
(100, 'alice@example.com', '$2a$10$mjHMYfmXZ179VX/kjwmubuoM3nH286msx0Jl2RKGNtkPxvZf1RTUC', 1),
(200, 'bob@example.com', '$2a$10$mjHMYfmXZ179VX/kjwmubuoM3nH286msx0Jl2RKGNtkPxvZf1RTUC', 1),
(300, 'kurt@example.com', '$2a$10$mjHMYfmXZ179VX/kjwmubuoM3nH286msx0Jl2RKGNtkPxvZf1RTUC', 1),
(400, 'patrick@example.com', '$2a$10$mjHMYfmXZ179VX/kjwmubuoM3nH286msx0Jl2RKGNtkPxvZf1RTUC', 1);
-- the password is '12345678' for all users to simplify