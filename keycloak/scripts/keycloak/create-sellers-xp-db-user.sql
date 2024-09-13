CREATE USER sellers_xp WITH ENCRYPTED PASSWORD 'password';

GRANT USAGE ON SCHEMA keycloak TO sellers_xp;
GRANT SELECT ON ALL TABLES IN SCHEMA keycloak TO sellers_xp;

ALTER USER sellers_xp set SEARCH_PATH = 'keycloak';
