FROM quay.io/keycloak/keycloak:25.0.0

COPY infrastructure/docker/quarkus.properties /opt/keycloak/conf/
COPY infrastructure/docker/monolith-users.jar /opt/keycloak/providers/
COPY infrastructure/docker/access-token-mapper.jar /opt/keycloak/providers/
COPY infrastructure/docker/jgroups-aws-bundle.jar /opt/keycloak/providers/
COPY infrastructure/docker/realm.json /opt/keycloak/data/import/realm.json
COPY infrastructure/docker/quarkus.properties /opt/keycloak/conf/
COPY infrastructure/docker/themes/ /opt/keycloak/themes/
