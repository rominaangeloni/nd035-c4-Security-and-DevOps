CREATE USER `keycloak` IDENTIFIED BY 'password';

GRANT USAGE ON `auth`.* TO `keycloak`@`%`;
GRANT SELECT ON `auth`.* TO `keycloak`@`%`;
