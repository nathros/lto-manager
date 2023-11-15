#!/bin/bash
JAR=build/libs/lto-manager-all.jar

HTTP_PORT=9000

if [ ! -f $JAR ]; then
	echo "$JAR not found, has it been built?"
	echo "Run: gradle shadowJar"
	exit 1
fi

# Remove console log
echo "See: config/logfile.log for messages and errors"
echo "Server: http://localhost:$HTTP_PORT"
java -Xmx512M -jar $JAR httpport $HTTP_PORT > /dev/null 2>&1

# Keep console log
#java -Xmx1024M -jar $JAR httpport $HTTP_PORT
