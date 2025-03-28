FROM registry.access.redhat.com/ubi9 AS ubi-micro-build
RUN mkdir -p /mnt/rootfs
RUN dnf install --installroot /mnt/rootfs curl jq --releasever 9 --setopt install_weak_deps=false --nodocs -y && \
    dnf --installroot /mnt/rootfs clean all && \
    rpm --root /mnt/rootfs -e --nodeps setup

FROM quay.io/keycloak/keycloak:25.0.2
COPY --from=ubi-micro-build /mnt/rootfs /

COPY infrastructure/docker/telegraf.conf /etc/telegraf/telegraf.conf
VOLUME /etc/telegraf

RUN mkdir -p /opt/keycloak/data/log

COPY infrastructure/docker/fluentd.conf /fluentd/etc/fluent.conf
VOLUME /fluentd/etc
VOLUME /opt/keycloak/data/log

# Keycloak config
COPY keycloak/config/themes /opt/keycloak/themes/
COPY keycloak/config/cache-ispn.xml /opt/keycloak/conf/
COPY keycloak/config/realm.json /opt/keycloak/data/import/realm.json

# Keycloak extensions
COPY keycloak/extensions/monolith-users/conf/quarkus.properties /opt/keycloak/conf/
COPY keycloak/extensions/monolith-users/target/monolith-users-jar-with-dependencies.jar /opt/keycloak/providers/
COPY keycloak/extensions/access-token-mapper/target/access-token-mapper.jar /opt/keycloak/providers/
COPY keycloak/extensions/jgroups-aws-bundle/target/jgroups-aws-bundle-jar-with-dependencies.jar /opt/keycloak/providers/
COPY keycloak/extensions/event-listener/target/event-listener-api-connect-jar-with-dependencies.jar /opt/keycloak/providers/

COPY infrastructure/docker/entrypoint.sh /opt/keycloak/bin/entrypoint.sh
ENTRYPOINT ["/opt/keycloak/bin/entrypoint.sh"]
CMD ["/opt/keycloak/bin/kc.sh", "start", "--import-realm"]
