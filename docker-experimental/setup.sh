#!/usr/bin/env bash

# setup mysql schema
docker cp ./tank_schema_setup.sql docker_db_1:/

## add connector/j jar to tomcat lib
#docker cp ./tomcat/mysql-connector-java-5.1.42-bin.jar docker_tomcat_1:/usr/local/tomcat/lib
#
## remove existing context.xml from tomcat and replace with db connection info included
#docker exec docker_tomcat_1 rm -Rf /usr/local/tomcat/conf/context.xml
##docker exec docker_tomcat_1 rm -Rf /usr/local/tomcat/webapps/*
#docker cp ./tomcat/context.xml docker_tomcat_1:/usr/local/tomcat/conf

docker exec docker_db_1 /bin/sh -c 'mysql -u tank -ptank tank </tank_schema_setup.sql'
