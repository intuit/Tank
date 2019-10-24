#!/bin/bash
INSTALL_DIR=$1
if [ -z "$1" ]
  then
    echo "No install dir supplied. Using `pwd`"
    INSTALL_DIR=`pwd`
fi
mkdir -p $INSTALL_DIR 2>/dev/null
echo "Installing all in one in $INSTALL_DIR"
echo "downloading and extracting tomcat 6..."
wget -O /tmp/apache-tomcat.tgz http://archive.apache.org/dist/tomcat/tomcat-6/v6.0.41/bin/apache-tomcat-6.0.41.tar.gz 2>/dev/null
tar -zxf /tmp/apache-tomcat.tgz -C $INSTALL_DIR 2>/dev/null
rm -f /tmp/apache-tomcat.tgz 2>/dev/null
ln -snf $INSTALL_DIR/apache-tomcat-6.0.41 $INSTALL_DIR/tomcat6 2>/dev/null
mkdir $INSTALL_DIR/tomcat6/db 2>/dev/null
mkdir $INSTALL_DIR/tomcat6/jars 2>/dev/null

echo "downloading and extracting agent-standalone..."
wget -O /tmp/agent-standalone-pkg.zip http://tank-public.s3-website-us-east-1.amazonaws.com/agent-standalone-pkg.zip 2>/dev/null
unzip -q -d $INSTALL_DIR /tmp/agent-standalone-pkg 2>/dev/null
rm -f /tmp/agent-standalone-pkg 2>/dev/null

echo "downloading and extracting support libraries..."
wget -O $INSTALL_DIR/tomcat6/lib/weld-tomcat-support-1.0.1-Final.jar http://central.maven.org/maven2/org/jboss/weld/servlet/weld-tomcat-support/1.0.1-Final/weld-tomcat-support-1.0.1-Final.jar 2>/dev/null
wget -O /$INSTALL_DIR/tomcat6/lib/h2-1.4.187.jar http://repo2.maven.org/maven2/com/h2database/h2/1.4.187/h2-1.4.187.jar 2>/dev/null
wget -O /$INSTALL_DIR/tomcat6/conf/server.xml http://tank-public.s3-website-us-east-1.amazonaws.com/server-all-in-one.xml 2>/dev/null
wget -O /$INSTALL_DIR/tomcat6/settings.xml http://tank-public.s3-website-us-east-1.amazonaws.com/settings-all-in-one.xml 2>/dev/null
wget -O $INSTALL_DIR/tomcat6/conf/context.xml http://tank-public.s3-website-us-east-1.amazonaws.com/context.xml 2>/dev/null

echo "downloading and installing tank war file..."
rm -fr $INSTALL_DIR/tomcat6/webapps/docs $INSTALL_DIR/tomcat6/webapps/examples $INSTALL_DIR/tomcat6/webapps/ROOT 2>/dev/null
wget -O $INSTALL_DIR/tomcat6/webapps/ROOT.war http://tank-public.s3-website-us-east-1.amazonaws.com/tank.war 2>/dev/null

echo "Creating start script at $INSTALL_DIR/start.sh ..."
cat << EOF > $INSTALL_DIR/start.sh
#!/bin/bash
echo "Starting Tomcat..."
export JAVA_OPTS="-Xms256m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=128m -Djava.awt.headless=true"
cd $INSTALL_DIR/tomcat6/
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
$INSTALL_DIR/tomcat6/bin/shutdown.sh &> /dev/null
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
