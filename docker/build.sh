#! /bin/bash

VERSION=$(cd ../ && git tag --points-at HEAD)
if [ -z "${VERSION}" ]; then
	VERSION=lastest
fi

docker build -t open-lto-manager:$VERSION .