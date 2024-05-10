#!/bin/bash
set -e # Exit on error

if [ "$EUID" -ne 0 ]; then 
  echo "Install script must be run as root"
  exit 1
fi

uninstall() {
	if [ -d "/opt/gradle/gradle-$1/" ]; then
		rm -rf /opt/gradle/gradle-$1/
		rm /usr/local/bin/gradle$1
		echo "Uninstalled Gradle $1"
	else
		echo "Gradle $1 not found"
	fi
}

if [ "$#" -ne 1 ]; then
	if [ -d "/opt/gradle/" ]; then
		set +e
		INSTALLED=($(ls -d /opt/gradle/* 2>/dev/null))
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
