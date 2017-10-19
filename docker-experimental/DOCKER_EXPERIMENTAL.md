# Tank

## local development setup with docker
* prereq: local instance of tomcat 8.5 installed - later do this with docker
* prereq: copy the context and mysql-connector to your tomcat installation

1. startup mysql using docker

 cd docker
 docker-compose up -d

 verify mysql should be running on port 3306

2. setup db schema

 from docker directory
 ./setup.sh

 note: this may take a few seconds (it's a large schema)

 verify by connecting to mysql however you prefer and validate tables are created

3. run the tomcat application server from your IDE

your browser should open up port 8080. login using:

 Login Name: tank
 Password:   password123

Next step: finish tomcat in docker setup


