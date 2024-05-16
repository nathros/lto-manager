#!/bin/bash
set -e # Exit on error

if [ "$EUID" -ne 0 ]; then 
  echo "Install script must be run as root"
  exit 1
fi

TMP_DIR=/tmp/gradle-tmp
INSTALL_LOC=/opt/gradle

rm -rf $TMP_DIR
mkdir -p $TMP_DIR

if [ "$#" -ne 1 ]; then
	echo "Getting latest Gradle 7.x version number"
	wget -q https://services.gradle.org/versions/all -O $TMP_DIR/all.json
	# Get latest version 7
	VER=$(cat $TMP_DIR/all.json | grep -m 1 'version" : "7') 	# Example: VER="version" : "7.6.4",
	VER=$(echo "${VER##*: }")									# Example: VER="7.6.4",
	VER=${VER:1:-2}												# Example: 7.6.4
	if [ -z "${VER}" ]; then
		echo "Failed to find Gradle version: $VER"
		rm -rf $TMP_DIR
		exit 1
	fi
else
	VER=$1
fi

echo "Installing Gradle $VER..."

if [ -d "$INSTALL_LOC/gradle-$VER/" ]; then
	echo "Gradle package $VER already installed in $INSTALL_LOC/gradle-$VER/"
else
	echo "Downloading: https://services.gradle.org/distributions/gradle-$VER-bin.zip to $TMP_DIR/"
	set +e
	wget -q https://services.gradle.org/distributions/gradle-$VER-bin.zip -O $TMP_DIR/gradle-$VER-bin.zip
	RET=$?
	if [ $RET -eq 0 ]; then
		echo "Extracting"
		unzip -q $TMP_DIR/gradle-$VER-bin.zip -d $TMP_DIR/contents
		echo "Move to $INSTALL_LOC/"
		mkdir -p $INSTALL_LOC
		mv $TMP_DIR/contents/* $INSTALL_LOC/gradle-$VER/
		rm -rf $TMP_DIR
		echo "Removed $TMP_DIR/"
		echo "Installed Gradle package $VER to $INSTALL_LOC/gradle-$VER/"
	elif [ $RET -eq 4 ]; then
		echo "Network error with https://services.gradle.org, check internet"
		rm -rf $TMP_DIR
		exit 1
	elif [ $RET -eq 8 ]; then
		echo "Failed to download gradle-$VER-bin.zip from https://services.gradle.org, does this version exist?"
		rm -rf $TMP_DIR
		exit 1
	else
		echo "Other error $RET"
		rm -rf $TMP_DIR
		exit 1
	fi
	set -e
fi

if [ -f "/usr/local/bin/gradle$VER" ]; then
    echo "Gradle launcher $VER already installed in /usr/local/bin/"
else
	echo -e "#!/bin/bash\n$INSTALL_LOC/gradle-$VER/bin/gradle \$@" > "/usr/local/bin/gradle$VER"
	chmod +x "/usr/local/bin/gradle$VER"
	echo "Installed Gradle launcher $VER to /usr/local/bin/gradle$VER"
fi

echo "Run Gradle via: gradle$VER"
