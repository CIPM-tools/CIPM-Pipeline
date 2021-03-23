#!/bin/bash

mkdir experiment-results

docker-compose rm -f
docker-compose up -d

java -jar "cipm-orchestration\scenario-orchestrator-0.0.1-all.jar" --config="cipm-orchestration\config.json"

timeout /t 600
docker cp compose_dmodel_1:/dmodel/evaluation experiment-results/

docker-compose stop