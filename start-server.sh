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
echo "Server started at: http://localhost:$HTTP_PORT"
while true
do
	java -Xmx256M -jar $JAR httpport $HTTP_PORT > /dev/null 2>&1
	if [ $? -eq 0 ]; then
		break; # Exit normally
	elif [ $? -eq 5 ]; then
		continue; # Loop as app has been updated, restart
	else
		echo "Exit with error code: $?"
		break;
	fi
done

# Keep console log
#java -Xmx1024M -jar $JAR httpport $HTTP_PORT
