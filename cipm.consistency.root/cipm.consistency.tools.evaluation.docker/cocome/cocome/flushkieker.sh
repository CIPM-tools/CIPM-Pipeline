#!/bin/bash
set -e

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