#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# Login to dockerhub
echo "Logging in to Docker Hub..."
docker login

# Build and push javafx-client-base
echo "Building javafx-client-base..."
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t ahallemberg/itp:javafx-client-base \
  --push \
  -f "${SCRIPT_DIR}/javafx-client-base.Dockerfile" \
  "${SCRIPT_DIR}"

# Build and push build-javafx-debian-application
echo "Building build-javafx-debian-application..."
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t ahallemberg/itp:build-javafx-debian-application \
  --push \
  -f "${SCRIPT_DIR}/build-javafx-debian-application.Dockerfile" \
  "${SCRIPT_DIR}"

# Build and push deploy-backend
echo "Building deploy-backend..."
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t ahallemberg/itp:deploy-backend \
  --push \
  -f "${SCRIPT_DIR}/deploy-backend.Dockerfile" \
  "${SCRIPT_DIR}"

echo "Deployment complete!"