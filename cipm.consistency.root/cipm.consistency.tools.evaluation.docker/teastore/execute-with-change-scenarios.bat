docker-compose rm -f
docker-compose up -d

java -jar "cipm-orchestration\scenario-orchestrator-0.0.1-all.jar" --config="cipm-orchestration\config.json" --scenarios="cipm-orchestration\scenarios\demo_scenario1\scenario.json"