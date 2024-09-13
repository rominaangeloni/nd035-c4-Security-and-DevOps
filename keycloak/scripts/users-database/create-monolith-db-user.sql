CREATE USER `keycloak` IDENTIFIED BY 'password';

GRANT USAGE ON `shnmpro2`.* TO `keycloak`@`%`;
GRANT SELECT ON `shnmpro2`.* TO `keycloak`@`%`;
