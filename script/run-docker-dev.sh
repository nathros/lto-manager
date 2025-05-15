#! /bin/bash

DIR=$(dirname "$0")
cd $DIR/..


docker run --rm -it --user "$UID" -v ./:/app --entrypoint bash open-lto-manager-dev:latest
