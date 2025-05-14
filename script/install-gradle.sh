#! /bin/bash

VER=7.6.4

mkdir /opt/gradle
chown $(whoami) /opt/gradle
mkdir /opt/gradle/gradle-$VER/
cd /opt/gradle/gradle-$VER/
wget https://services.gradle.org/distributions/gradle-$VER-bin.zip
unzip gradle-$VER-bin.zip
rm gradle-$VER-bin.zip
export PATH=$PATH:/opt/gradle/gradle-$VER/bin
echo export PATH=$PATH:/opt/gradle/gradle-$VER/bin >> ~/.bashrc
