#!/bin/bash
set -e

echo '####################  pull changes   ####################### '

##############################################################################

cd /usr/src/cocome/cocome-maven-project
git checkout cipm-consistency-evaluation
git pull

cd /usr/src/serviceadapter
git pull

echo '####################  pulled changes   ####################### '

echo '### undeploy old ###'

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin --host localhost -p 8048 --user admin -W /usr/src/glassfish/glassfish4/glassfish/passwordfile undeploy cloud-web-frontend

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin --host localhost -p 8148 --user admin -W /usr/src/glassfish/glassfish4/glassfish/passwordfile undeploy store-logic-ear

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin --host localhost -p 8248 --user admin -W /usr/src/glassfish/glassfish4/glassfish/passwordfile undeploy service-adapter-ear

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin --host localhost -p 8348 --user admin -W /usr/src/glassfish/glassfish4/glassfish/passwordfile undeploy enterprise-logic-ear

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin --host localhost -p 8448 --user admin -W /usr/src/glassfish/glassfish4/glassfish/passwordfile undeploy cloud-registry-service

echo '### restart services ###'
##############################################################################
#restart needed because of changed attributes like password
#Important notice: restart of the glassfish domains or start in general has to take playce in this order!
echo '########## restart database ##################'
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-database 
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin start-database 

echo '########## restart domain REGISTRY ##################'
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain registry
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin start-domain registry

echo '########## restart domain ADAPTER ##################'
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain adapter
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin start-domain  adapter

echo '########## restart domain WEB ##################'
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain web
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin start-domain  web

echo '########## restart domain STORE ##################'
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain store
/usr/src/glassfish/glassfish4/glassfish/bin/asadmin start-domain  store

echo '########## restart domain ENTERPRISE ##################'
#/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain enterprise
#/usr/src/glassfish/glassfish4/glassfish/bin/asadmin start-domain enterprise

echo '### deploy new###'
cd /usr/src/cocome/cocome-maven-project && mvn -s /usr/src/cocome-maven-project-setting.xml -T 1C clean compile package 

cd /usr/src/cocome/cocome-maven-project && mvn -s /usr/src/cocome-maven-project-setting.xml install -DskipTests

cd /usr/src/serviceadapter && mvn -s /usr/src/serviceadapter-settings.xml -T 1C clean compile package

cd /usr/src/serviceadapter && mvn -s /usr/src/serviceadapter-settings.xml install -DskipTests


