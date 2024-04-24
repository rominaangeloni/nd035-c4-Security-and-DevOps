# iam
Identity and access manager (Keycloak)

oauth2c "http://localhost:9090/realms/wallapop-connect" \
--browser-timeout 30s  \
--grant-type authorization_code  \
--pkce  \
--client-id portal-hero  \
--client-secret OiICRTehnwyXkKOVRAFFbTKNjAp2ez4w  \
--auth-method client_secret_basic  \
--response-types code  \
--response-mode query 