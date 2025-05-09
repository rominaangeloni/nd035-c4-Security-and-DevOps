#!/bin/ash

sed -i "s@PROMETHEUS_USERNAME@${PROMETHEUS_USERNAME}@g" "/prometheus/config/prometheus.yaml"
sed -i "s@PROMETHEUS_PASSWORD@${PROMETHEUS_PASSWORD}@g" "/prometheus/config/prometheus.yaml"
sed -i "s@JOB_NAME_SUFFIX@${JOB_NAME_SUFFIX}@g" "/prometheus/config/prometheus.yaml"

export TASK_ID="no-task-id"

get_task_id() {
  TASK_ID=$(cat "$ECS_CONTAINER_METADATA_FILE" | sed -nE 's/.*TaskARN.*\/([a-z]+)\/([a-z0-9]+).*/\1/p')
  if [[ $TASK_ID == "beta" || $TASK_ID == "prod" ]]; then
    TASK_ID=$(cat "$ECS_CONTAINER_METADATA_FILE" | sed -nE 's/.*TaskARN.*\/([a-z]+)\/([a-z0-9]+).*/\2/p')
  fi
}

if [ -f "$ECS_CONTAINER_METADATA_FILE" ]; then
    get_task_id
fi

sed -i "s@TASK_ID@${TASK_ID}@g" "/prometheus/config/prometheus.yaml"
