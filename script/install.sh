#! /bin/bash

ABORT=
error_message () {
	echo "Error: $1"
	ABORT=true
} 


# Check Java
JAVA_VERSION=$(java --version)
JAVA_VERSION_MIN=17
if [ $? -eq 0 ]; then
	JAVA_VERSION=$(echo $JAVA_VERSION | cut -d " " -f 2)
    
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d "." -f 1)
    if [ "$JAVA_MAJOR_VERSION" -ge "$JAVA_VERSION_MIN" ]; then
    	echo "Found compatible java version: $JAVA_VERSION"
    else
    	error_message "Found incompatible java: $JAVA_VERSION"
    fi
else
    error_message "Java not installed"
fi


# Check LTFS
LTFS_VERSION=$(ltfs --version 2>&1)
if [ $? -eq 0 ]; then
	LTFS_VERSION=$(echo $LTFS_VERSION | cut -d " " -f 3)
	echo "Found LTFS version: $LTFS_VERSION"
else
    echo "WARNING: LTFS is not installed, some features will not be enabled"
    while true; do
    	read -p "Install anyway (y/n)? " yn
    	if [[ "$yn" == "n" ]]; then
    		ABORT=true
    		break
		elif [[ "$yn" == "y" ]]; then
			break    		
    	fi
    done
fi

if [ ! -z $ABORT ]; then
	echo "Install aborted"
	exit 1 
fi

