#!/bin/bash
set -e # Exit on error

if [ "$EUID" -ne 0 ]; then 
  echo "Install script must be run as root"
  exit 1
fi

rm -rf /tmp/gradle-tmp
mkdir -p /tmp/gradle-tmp

if [ "$#" -ne 1 ]; then
	echo "Getting latest Gradle 7.x version number"
	wget -q https://services.gradle.org/versions/all -O /tmp/gradle-tmp/all.json
	# Get latest version 7
	VER=$(cat /tmp/gradle-tmp/all.json | grep -m 1 'version" : "7') 		# Example: VER="version" : "7.6.4",
	VER=$(echo "${VER##*: }")												# Example: VER="7.6.4",
	VER=${VER:1:-2}															# Example: 7.6.4
	if [ -z "${VER}" ]; then
		echo "Failed to find Gradle version: $VER"
		rm -rf /tmp/gradle-tmp
		exit 1
	fi
else
	VER=$1
fi

echo "Installing Gradle $VER..."

if [ -d "/opt/gradle/gradle-$VER/" ]; then
	echo "Gradle $VER already installed in /opt"
else
	echo "Downloading: https://services.gradle.org/distributions/gradle-$VER-bin.zip to /tmp/gradle-tmp/"
	set +e
	wget -q https://services.gradle.org/distributions/gradle-$VER-bin.zip -O /tmp/gradle-tmp/gradle-$VER-bin.zip
	RET=$?
	if [ $RET -eq 0 ]; then
		echo "Extracting"
		unzip -q /tmp/gradle-tmp/gradle-$VER-bin.zip -d /tmp/gradle-tmp/contents
		echo "Move to /opt/gradle/"
		mkdir -p /opt/gradle
		mv /tmp/gradle-tmp/contents/* /opt/gradle/gradle-$VER/
		rm -rf /tmp/gradle-tmp
		echo "Removed /tmp/gradle-tmp/"
	elif [ $RET -eq 4 ]; then
		echo "Network error with https://services.gradle.org, check internet"
		rm -rf /tmp/gradle-tmp
		exit 1
	elif [ $RET -eq 8 ]; then
		echo "Failed to download gradle-$VER-bin.zip from https://services.gradle.org, does this version exist?"
		rm -rf /tmp/gradle-tmp
		exit 1
	else
		echo "Other error $RET"
		rm -rf /tmp/gradle-tmp
		exit 1
	fi
	set -e
fi

if [ -f "/usr/local/bin/gradle$VER" ]; then
    echo "Gradle $VER already installed in /usr/local/bin/"
else
	echo -e "#!/bin/bash\n/opt/gradle/gradle-$VER/bin/gradle \$@" > "/usr/local/bin/gradle$VER"
	chmod +x "/usr/local/bin/gradle$VER"
	echo "Installed Gradle $VER to /opt/gradle/gradle-$VER/"
fi

echo "Run Gradle via: gradle$VER"
