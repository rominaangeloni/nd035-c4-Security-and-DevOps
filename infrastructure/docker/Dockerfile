FROM registry.access.redhat.com/ubi9 AS ubi-micro-build
RUN mkdir -p /mnt/rootfs
RUN dnf install --installroot /mnt/rootfs curl jq nmap --releasever 9 --setopt install_weak_deps=false --nodocs -y && \
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

COPY infrastructure/docker/quarkus.properties /opt/keycloak/conf/
COPY infrastructure/docker/monolith-users.jar /opt/keycloak/providers/
COPY infrastructure/docker/access-token-mapper.jar /opt/keycloak/providers/
COPY infrastructure/docker/jgroups-aws-bundle.jar /opt/keycloak/providers/
COPY infrastructure/docker/realm.json /opt/keycloak/data/import/realm.json
COPY infrastructure/docker/themes/ /opt/keycloak/themes/
COPY infrastructure/docker/custom-cache-ispn.xml /opt/keycloak/conf/
