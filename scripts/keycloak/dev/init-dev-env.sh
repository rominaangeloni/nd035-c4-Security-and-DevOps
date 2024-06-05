#!/bin/bash

echo "Building extensions"

mvn -f extensions/access-token-mapper/pom.xml clean install

mvn -f extensions/monolith-users/pom.xml clean install
