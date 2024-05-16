#!/bin/bash
set -e # Exit on error

if [ "$EUID" -ne 0 ]; then 
  echo "Install script must be run as root"
  exit 1
fi

INSTALL_LOC=/opt/gradle

uninstall() {
	PACKAGE=$INSTALL_LOC/gradle-$1/
	LAUNCH=/usr/local/bin/gradle$1
	if [ -d $PACKAGE ]; then
		rm -rf $PACKAGE
		echo "Uninstalled Gradle $1 package: $PACKAGE"
	else
		echo "Gradle $1 package not found"
	fi
	if [ -f $LAUNCH ]; then
		rm -f $LAUNCH
		echo "Uninstalled Gradle $1 launcher: $LAUNCH"
	else
		echo "Gradle $1 launcher not found"
	fi
}

if [ "$#" -ne 1 ]; then
	if [ -d "$INSTALL_LOC/" ]; then
		set +e
		INSTALLED=($(ls -d $INSTALL_LOC/* 2>/dev/null))
		set -e
		if [ "${#INSTALLED[@]}" -eq 0 ]; then
			echo "Gradle not installed"
		elif [ "${#INSTALLED[@]}" -ne 1 ]; then
			# Found more than one version
			echo "Found versions:"
			for i in "${INSTALLED[@]}"
			do
				i=${i:19}
				echo "$i"
			done
			echo "Multiple versions found, please specify which one, example: $BASH_SOURCE ${INSTALLED[0]:19}"
		else
			uninstall "${INSTALLED[0]:19}"
		fi
	else
		echo "No installed versions found"
	fi
else
	uninstall "$1"
fi
