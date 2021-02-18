cd "cipm-consistency"
docker build -t "cipm/consistency" .

cd "../cipm-teastore-load"
docker build -t "cipm/teastore-load" .