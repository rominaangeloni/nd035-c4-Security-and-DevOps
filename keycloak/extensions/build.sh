#!/bin/bash

echo "Preparing realms"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
"$SCRIPT_DIR/../scripts/realm/merger.sh" "$@"

echo "Building extensions"

./mvnw -f access-token-mapper/pom.xml clean install
./mvnw -f monolith-users/pom.xml clean install
./mvnw -f jgroups-aws-bundle/pom.xml clean install
./mvnw -f event-listener/pom.xml clean install
