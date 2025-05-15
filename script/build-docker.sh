#! /bin/bash

# This build Docker image for publishing the application

DIR=$(dirname "$0")
cd $DIR/..

VERSION=$(git tag --points-at HEAD)
if [ -z "${VERSION}" ]; then
	VERSION=lastest
fi

docker build -t open-lto-manager:$VERSION .