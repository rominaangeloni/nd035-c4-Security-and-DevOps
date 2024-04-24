# IAM
Identity and access manager (Keycloak)

1- Run docker compose 

2- Select `wallapop-connect` real

3- Create a user with password (complete all data and put "email verified" in false)

4- Download [oauth2c](https://github.com/cloudentity/oauth2c) and run :
```
oauth2c "http://localhost:9090/realms/wallapop-connect" \
--browser-timeout 30s  \
--grant-type authorization_code  \
--pkce  \
--client-id portal-hero  \
--client-secret OiICRTehnwyXkKOVRAFFbTKNjAp2ez4w  \
--auth-method client_secret_basic  \
--response-types code  \
--response-mode query 
```