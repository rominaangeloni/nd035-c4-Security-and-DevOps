#!/bin/bash

url="http://localhost:9000/health"
max_attempts=120
attempt=1

echo "Waiting for Keycloak to complete booting up..."

while [ $attempt -le $max_attempts ]
do
    response_code=$(curl -s -o /dev/null -w "%{http_code}" "$url")

    if [ "$response_code" -eq 200 ]; then
        echo "Success! Keycloak is up :)"
        break
    fi

    echo "Attempt $attempt failed. Response code: $response_code. Retrying in 1 second..."
    sleep 1
    attempt=$((attempt + 1))
done

if [ $attempt -gt $max_attempts ]; then
    echo "Timeout: Did not receive status code 200 from Keycloak within 2 minutes."
fi