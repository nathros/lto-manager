#! /bin/bash

# This build Docker image for development and building the application

# Test
# docker run --rm -it --entrypoint bash debian:12.10
# docker run --rm -it --entrypoint bash open-lto-manager-dev:latest

DIR=$(dirname "$0")
cd $DIR/..


docker build -f Dockerfile.dev -t open-lto-manager-dev:latest .