#!/bin/bash

# step 1 user will provide the Dockerfile path as argument
# step 2 build the docker image
# step 3 tag the docker image 
# step 4 login to docker hub
# step 5 push the docker image to docker hub
echo "Starting Docker build and push process..."
echo "----------------prerequisite----------------"
echo "1. Ensure you have Docker installed and running."
echo "2. You are logged in to Docker Hub using 'docker login'."

Dockerfile_PATH="$1"

if [ -z "$Dockerfile_PATH" ]; then
    echo "Dockerfile path argument is missing!"
    exit 1
fi
echo "Building Docker image..."

read -p "Enter Docker image name: " IMAGE_NAME
read -p "Enter Docker tag (default: latest): " TAG
if [ -z "$IMAGE_NAME" ]; then
    echo "Docker image name is required!"
    exit 1
fi

if [ -z "$TAG" ]; then
    TAG="latest"
fi

DOCKER_USERNAME="kjvns"

docker build -t "$DOCKER_USERNAME/$IMAGE_NAME:$TAG" "$Dockerfile_PATH"
if [ $? -ne 0 ]; then
    echo "Docker build failed!"
    exit 1
fi

docker push "$DOCKER_USERNAME/$IMAGE_NAME:$TAG"
if [ $? -ne 0 ]; then
    echo "Docker push failed!"
    exit 1
fi
echo "Docker image pushed successfully: $DOCKER_USERNAME/$IMAGE_NAME:$TAG"