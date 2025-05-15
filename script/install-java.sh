#! /bin/bash
set -e

VER=$1
ARCH=x64
DIST="_linux_"

mkdir -p /opt/java
cd /opt/java
URL=$(curl -s "https://api.github.com/repos/adoptium/temurin${VER}-binaries/releases/latest" | jq -r --arg arch "$ARCH" --arg suffix "$DIST" '.assets[] | select(.name | contains("hotspot") and contains("jre") and contains($arch) and contains($suffix) and endswith(".tar.gz")) | .browser_download_url')
wget $URL
tar -xvzf OpenJDK*
rm *.tar.gz
JAVA_DIR=$(ls /opt/java | grep jdk)

echo "export PATH=\$PATH:/opt/java/$JAVA_DIR/bin" >> ~/.bashrc
echo "export JAVA_HOME=/opt/java/$JAVA_DIR" >> ~/.bashrc
source ~/.bashrc
