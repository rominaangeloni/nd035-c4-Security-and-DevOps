#!/bin/bash

echo "Building extensions"

./mvnw -f access-token-mapper/pom.xml clean install
./mvnw -f monolith-users/pom.xml clean install
./mvnw -f jgroups-aws-bundle/pom.xml clean install
