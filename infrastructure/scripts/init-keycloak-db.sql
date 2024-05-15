/* Note: the script must be run while connected to the keycloak database */

create user keycloak with password 'password'
    createdb;

grant keycloak to postgres;

alter database keycloak owner to keycloak;

create schema keycloak;
alter schema keycloak owner to keycloak;
