#!/bin/bash
echo "Starting REST interface."
echo "Arguments: $1"

# updating
cd /dmodel/

# start
java -Dserver.port=8080 -jar "dmodel.jar" --config="config.yml"