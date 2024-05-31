FROM quay.io/keycloak/keycloak:24.0.4

COPY infrastructure/docker/monolith-users.jar /opt/keycloak/providers/
COPY infrastructure/docker/access-token-mapper.jar /opt/keycloak/providers/
COPY infrastructure/docker/quarkus.properties /opt/keycloak/conf/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
