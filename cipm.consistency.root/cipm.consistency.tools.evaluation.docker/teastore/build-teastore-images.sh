#!/bin/bash

mkdir teastore

cd teastore
git clone https://github.com/dmonsch/TeaStore

cd TeaStore
git checkout cipm-consistency-evaluation
git pull
mvn clean install -Dcheckstyle.skip -Dmaven.test.skip

cd "services/tools.descartes.teastore.auth"
docker build -t "teastore-auth" .

cd "../../"

cd "services/tools.descartes.teastore.image"
docker build -t "teastore-image" .

cd "../../"

cd "services/tools.descartes.teastore.persistence"
docker build -t "teastore-persistence" .

cd "../../"

cd "services/tools.descartes.teastore.recommender"
docker build -t "teastore-recommender" .

cd "../../"

cd "services/tools.descartes.teastore.registry"
docker build -t "teastore-registry" .

cd "../../"

cd "services/tools.descartes.teastore.webui"
docker build -t "teastore-webui" .