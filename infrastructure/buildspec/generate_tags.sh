#!/usr/bin/env bash

set -eu

cd "$(dirname "$0")"

TEMPLATES_DIR="../aws"

generate_configuration_from_template() {
  local template=$1
  local env=$2
  local role=$3

  local template_in="$TEMPLATES_DIR/${template}_conf.json"
  local template_out="$TEMPLATES_DIR/${template}_conf_${env}.json"

  copy_existing_template_or_empty "$template_in" "$template_out"
  populate_configuration "$template_out" "$env" "$role"
}

copy_existing_template_or_empty() {
  local source=$1
  local target=$2

  test -f "$source" || touch "$source"
  cp "$source" "$target"
}

generate_configuration() {
  local template=$1
  local env=$2
  local role=$3

  local template_out="$TEMPLATES_DIR/${template}_conf_${env}.json"

  populate_configuration "$template_out" "$env" "$role"
}

populate_configuration() {
  local template=$1
  local env=$2
  local role=$3

  append_tags "$template" "$env"
  populate_variables "$template_out" "$env" "$role"
}

append_tags() {
  local template=$1
  local env=$2

  local tags="$TEMPLATES_DIR/tags.json"

  jq -s add "$template" "$tags" > "${template}.tmp" && mv "${template}.tmp" "$template"
}

populate_variables() {
  local template=$1
  local env=$2
  local role=$3

  sed -i "s/%ENV%/$env/" "$template"
  sed -i "s/%ROLE%/$role/" "$template"
}

for env in "beta" "prod"
do
  generate_configuration_from_template "pipeline" $env "deployment"
  generate_configuration_from_template "keycloak" $env "service"
  generate_configuration_from_template "database" $env "database"
  generate_configuration_from_template "alarms-topics" $env "service"
done
