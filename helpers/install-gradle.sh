#!/bin/bash
set -e # Exit on error

if [ "$EUID" -ne 0 ]; then 
  echo "Install script must be run as root"
  exit 1
fi

export VER=7.2
rm -rf /tmp/gradle-tmp
mkdir -p /tmp/gradle-tmp
wget https://services.gradle.org/distributions/gradle-$VER-bin.zip -O /tmp/gradle-tmp/gradle-$VER-bin.zip
unzip /tmp/gradle-tmp/gradle-$VER-bin.zip -d /tmp/gradle-tmp/contents
mkdir -p /opt/gradle
mv /tmp/gradle-tmp/contents/* /opt/gradle/gradle-$VER/
rm -rf /tmp/gradle-tmp

echo -e "#!/bin/bash\n/opt/gradle/gradle-$VER/bin/gradle $@" > "/usr/local/bin/gradle$VER"
chmod +x "/usr/local/bin/gradle$VER"

echo "Installed Gradle 7.2 to /opt/gradle/gradle-$VER/"
echo "Run via: gradle$VER"
