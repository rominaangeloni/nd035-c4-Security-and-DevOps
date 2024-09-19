#!/bin/bash

export TASK_ID="no-task-id"

get_task_id() {
  TASK_ID=$(curl -s "$ECS_CONTAINER_METADATA_URI_V4/task" | jq -r ".TaskARN" | cut -d "/" -f 3)
}

replace_task_id_in_fluent_config() {
  sed -i "s#<TASK_ID>#$TASK_ID#g" /fluentd/etc/fluent.conf
}

replace_task_id_in_telegraf_config() {
  sed -i "s#<TASK_ID>#$TASK_ID#g" /etc/telegraf/telegraf.conf
}

get_task_id
replace_task_id_in_fluent_config
replace_task_id_in_telegraf_config
