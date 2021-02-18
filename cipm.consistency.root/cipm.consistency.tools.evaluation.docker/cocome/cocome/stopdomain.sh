#!/bin/bash



/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-database
echo '####################  databse stopped   ####################### ' 

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain registry
echo '####################  registry stopped  ####################### '

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain adapter
echo '####################  adapter stopped   ####################### '

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain web
echo '####################  web stopped   ####################### '

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain store
echo '####################  Store stopped  ####################### '

/usr/src/glassfish/glassfish4/glassfish/bin/asadmin stop-domain enterprise
echo '####################  enterprise stopped   ####################### '









