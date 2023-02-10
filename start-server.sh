#!/bin/bash
JAR=build/libs/lto-manager-all.jar

HTTP_PORT=9000

if [ ! -f $JAR ]; then
	echo "$JAR not found, has it been built?"
	echo "Build: gradle shadowJar"
	exit 1
fi

# Remove console log
echo "See: config/logfile.log for messages and errors"
echo "Server: http://localhost:$HTTP_PORT"
java -Xmx1000M -Xms1000M -jar $JAR httpport $HTTP_PORT > /dev/null 2>&1

# Keep console log
#java -Xmx200M -Xms200M -jar $JAR httpport $HTTP_PORT
