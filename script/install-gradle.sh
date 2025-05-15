#! /bin/bash
set -e

VER=8.14

mkdir -p /opt/gradle
chown $(whoami) /opt/gradle
cd /opt/gradle
wget https://services.gradle.org/distributions/gradle-$VER-bin.zip
unzip gradle-$VER-bin.zip
rm gradle-$VER-bin.zip
echo "export PATH=\$PATH:/opt/gradle/gradle-$VER/bin" >> ~/.bashrc
source ~/.bashrc
