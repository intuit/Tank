#!/bin/bash
INSTALL_DIR=$1
if [ -z "$1" ]
  then
    echo "No install dir supplied. Using `pwd`"
    INSTALL_DIR=`pwd`
fi
mkdir -p $INSTALL_DIR 2>/dev/null
echo "Installing all in one in $INSTALL_DIR"
echo "downloading and extracting tomcat 9..."
wget -O /tmp/apache-tomcat.tgz http://archive.apache.org/dist/tomcat/tomcat-9/v9.0.39/bin/apache-tomcat-9.0.39.tar.gz 2>/dev/null
tar -zxf /tmp/apache-tomcat.tgz -C $INSTALL_DIR 2>/dev/null
rm -f /tmp/apache-tomcat.tgz 2>/dev/null
ln -snf $INSTALL_DIR/apache-tomcat-9.0.39 $INSTALL_DIR/tomcat 2>/dev/null
mkdir $INSTALL_DIR/tomcat/db 2>/dev/null
mkdir $INSTALL_DIR/tomcat/jars 2>/dev/null

echo "downloading and extracting agent-standalone..."
wget -O /tmp/agent-standalone-pkg.zip https://github.com/intuit/Tank/releases/download/3.1.1-SNAPSHOT/agent-standalone-pkg.zip 2>/dev/null
unzip -q -d $INSTALL_DIR /tmp/agent-standalone-pkg 2>/dev/null
rm -f /tmp/agent-standalone-pkg 2>/dev/null

echo "downloading and extracting support libraries..."
wget -O /$INSTALL_DIR/tomcat/lib/h2-1.4.200.jar https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar 2>/dev/null
wget -O /$INSTALL_DIR/tomcat/settings.xml https://github.com/intuit/Tank/blob/master/assets/settings-all-in-one.xml 2>/dev/null

echo "downloading and installing tank war file..."
rm -fr $INSTALL_DIR/tomcat/webapps/docs $INSTALL_DIR/tomcat/webapps/examples $INSTALL_DIR/tomcat/webapps/ROOT 2>/dev/null
wget -O $INSTALL_DIR/tomcat/webapps/ROOT.war https://github.com/intuit/Tank/releases/download/3.1.1-SNAPSHOT/tank.war 2>/dev/null

echo "Creating context file at $INSTALL_DIR/start.sh ..."
cat << EOF > $INSTALL_DIR/tomcat/conf/context.xml
#!/bin/bash
<?xml version="1.0" encoding="UTF-8"?>

<Context>
    <Resource name="jdbc/watsDS" auth="Container"
        type="javax.sql.DataSource"
        driverClassName="org.h2.Driver"
        url="jdbc:h2:mem:tank"
        username="sa" password=""
        maxActive="20" maxIdle="10" maxWait="-1"/>
</Context>
EOF
chmod 644 $INSTALL_DIR/tomcat/conf/context.xml 2>/dev/null

echo "Creating start script at $INSTALL_DIR/start.sh ..."
cat << EOF > $INSTALL_DIR/start.sh
#!/bin/bash
echo "Starting Tomcat..."
export JAVA_OPTS="-Xms256m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=128m -Djava.awt.headless=true"
cd $INSTALL_DIR/tomcat/
bin/startup.sh &> /dev/null
echo "Tomcat started."
cd $INSTALL_DIR/agent-standalone/
echo "Starting Agent..."
./run.sh &> /dev/null
echo "Agent started."
EOF
chmod 755 $INSTALL_DIR/start.sh 2>/dev/null

echo "Creating stop script at $INSTALL_DIR/stop.sh..."
cat << EOF > $INSTALL_DIR/stop.sh
#!/bin/bash
echo "Stopping Tomcat..."
$INSTALL_DIR/tomcat/bin/shutdown.sh &> /dev/null
echo "Stopping Agent..."
kill $(ps aux | grep '[a]gent-standalone' | awk '{print $2}') &> /dev/null
kill $(ps aux | grep '[a]piharness' | awk '{print $2}') &> /dev/null
kill $(ps aux | grep '[t]omcat' | awk '{print $2}') &> /dev/null
echo "Tomcat and Agents stopped."
EOF
chmod 755 $INSTALL_DIR/stop.sh 2>/dev/null

mkdir -p $INSTALL_DIR/agent-standalone
cat << EOF > $INSTALL_DIR/agent-standalone/run.sh
#!/bin/bash
controller="http://localhost:8080"
host=`hostname`
capacity=400
nohup java -jar agent-standalone-all.jar -controller=$controller -host=$host -capacity=$capacity &
EOF

echo "Installing all in one in $INSTALL_DIR"
echo "Run $INSTALL_DIR/start.sh to start the all in one server and $INSTALL_DIR/stop.sh to kill it"
