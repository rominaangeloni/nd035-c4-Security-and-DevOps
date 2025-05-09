ARG ECR
ARG VERSION
FROM ${ECR}prom/prometheus:$VERSION

ENV CONFIG_FILE config/prometheus.yaml
ENV LOG_LEVEL info

COPY --chown=nobody:nobody prometheus.yaml $CONFIG_FILE
COPY configure_prometheus.sh /bin/

ENTRYPOINT /bin/configure_prometheus.sh; \
           /bin/prometheus \
            --config.file=$CONFIG_FILE \
            --log.level=$LOG_LEVEL \
            --web.listen-address=0.0.0.0:9099
