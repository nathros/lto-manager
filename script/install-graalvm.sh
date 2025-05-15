#! /bin/bash
set -e

VER=$1

mkdir /opt/graalvm
cd /opt/graalvm
wget https://download.oracle.com/graalvm/${VER}/latest/graalvm-jdk-${VER}_linux-x64_bin.tar.gz
tar -xvzf graalvm-jdk-${VER}_linux-x64_bin.tar.gz
rm graalvm-jdk-${VER}_linux-x64_bin.tar.gz

echo "export PATH=\$PATH:/opt/graalvm/graalvm-jdk-$VER.0.7+8.1/bin" >> ~/.bashrc
echo "export GRAALVM_HOME=/opt/graalvm/graalvm-jdk-$VER.0.7+8.1" >> ~/.bashrc
source ~/.bashrc
