#!/bin/bash

VERSION=v3.1.0
IMAGE_TAG="${VERSION}-iam"

function ecr_image_exists() {
  local ecr=$1
  local imageTag=$2
  aws ecr describe-images \
    --repository-name "$ecr" \
    --image-ids imageTag="$imageTag" \
    2> /dev/null \
    | jq '.imageDetails[] .imageTags[]' || true
  return $?
}

if [[ -n $(ecr_image_exists 'prom/prometheus' ${IMAGE_TAG}) ]]; then
  echo "Prometheus '${IMAGE_TAG}' already built. Won't recreate it."
  exit 0
fi

echo "Building prometheus image"
docker build -f infrastructure/docker/prometheus/Dockerfile \
  -t ${ECR}prom/prometheus:${IMAGE_TAG} \
  --build-arg ECR=${ECR} \
  --build-arg VERSION=${VERSION} \
  infrastructure/docker/prometheus/

echo "Push prometheus custom image to ECR"
docker push ${ECR}prom/prometheus:${IMAGE_TAG}
