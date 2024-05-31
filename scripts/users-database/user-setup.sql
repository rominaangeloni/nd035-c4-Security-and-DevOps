create database IF NOT EXISTS monolith;
use monolith;

create table IF NOT EXISTS usr_usr
(
    uuid_                 varchar(75)   null,
    usrId                 bigint        not null
        primary key,
    createDate            datetime      null,
    modifiedDate          datetime      null,
    password_             varchar(75)   null,
    passwordEncrypted     tinyint       null,
    passwordReset         tinyint       null,
    passwordModifiedDate  datetime      null,
    digest                varchar(255)  null,
    reminderQueryQuestion varchar(75)   null,
    reminderQueryAnswer   varchar(75)   null,
    graceLoginCount       int           null,
    screenName            varchar(255)  null,
    emailAddress          varchar(150)  null,
    facebookId            bigint        null,
    openId                varchar(1024) null,
    portraitId            bigint        null,
    pictureId             bigint        null,
    languageId            varchar(75)   null,
    timeZoneId            varchar(75)   null,
    greeting              varchar(255)  null,
    comments              longtext      null,
    firstName             varchar(75)   null,
    middleName            varchar(75)   null,
    lastName              varchar(75)   null,
    jobTitle              varchar(100)  null,
    loginDate             datetime      null,
    loginIP               varchar(75)   null,
    lastLoginDate         datetime      null,
    lastLoginIP           varchar(75)   null,
    lastFailedLoginDate   datetime      null,
    failedLoginAttempts   int           null,
    lockout               tinyint       null,
    lockoutDate           datetime      null,
    agreedToTermsOfUse    tinyint       null,
    emailAddressVerified  tinyint       null,
    status                int           null,
    sexId                 int           null,
    birthday              datetime      null,
    mainLocationId        bigint        null,
    latitude              double        null,
    longitude             double        null,
    ipCountryCode         varchar(4)    null,
    userEmailAddress      varchar(150)  null
)
    engine = InnoDB;

create table IF NOT EXISTS user_perks
(
    perk_id     bigint       not null,
    user_id     bigint       not null,
    type        varchar(255) not null,
    category_id bigint       not null,
    period_end  timestamp    not null,
    primary key (perk_id, user_id, category_id)
)
    engine = InnoDB;

INSERT INTO usr_usr (usrId, firstName, lastname, userEmailAddress, password_) VALUES
(1, 'Alice', 'Cooper', 'alice@example.com', '$2a$10$/f9YLESunE6yoQF8/CtlOOuFogfy4bkyggnrINXXuHIQbGM02RPV6'),
(2, 'Bob', 'Sponge', 'bob@example.com', '$2a$10$/f9YLESunE6yoQF8/CtlOOuFogfy4bkyggnrINXXuHIQbGM02RPV6'),
(3, 'Kurt', 'Cobain', 'kurt@example.com', '$2a$10$/f9YLESunE6yoQF8/CtlOOuFogfy4bkyggnrINXXuHIQbGM02RPV6'),
(4, 'Patrick', 'Store', 'patrick@example.com', '$2a$10$/f9YLESunE6yoQF8/CtlOOuFogfy4bkyggnrINXXuHIQbGM02RPV6');

INSERT INTO user_perks (perk_id, user_id, type, category_id, period_end) VALUES
(10, 1, 'USER_PROFILE_FEATURED', 100, FROM_UNIXTIME(1894956553, '%Y-%m-%d %H:%i:%s')),
(10, 2, 'USER_PROFILE_FEATURED', 100, FROM_UNIXTIME(1894956553, '%Y-%m-%d %H:%i:%s')),
(10, 3, 'USER_PROFILE_FEATURED', 100, FROM_UNIXTIME(1894956553, '%Y-%m-%d %H:%i:%s'));