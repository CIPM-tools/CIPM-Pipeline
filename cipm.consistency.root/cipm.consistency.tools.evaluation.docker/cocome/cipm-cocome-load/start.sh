#!/bin/bash
echo "Starting load orchestrator interface."

# start
java -Dserver.port=8080 -jar "load.jar" --jmeterPath="/jmeter/apache-jmeter-5.4/" --loadFile="default.jmx"