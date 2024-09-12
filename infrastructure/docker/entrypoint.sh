#!/bin/sh

# We've detected a bug (not published yet) on how jgroups auto-discovers ECS instance nodes, we'll make use of this
# new env var rather than default jgroups ec2 parameters
export ECS_INSTANCE_PRIVATE_ADDRESS=$(curl ${ECS_CONTAINER_METADATA_URI} | jq -r .Networks[0].IPv4Addresses[0])

# This will exec the CMD from your Dockerfile
exec "$@"
