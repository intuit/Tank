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
echo "#!/bin/bash">$INSTALL_DIR/start.sh
echo "echo \"Starting Tomcat...\"">>$INSTALL_DIR/start.sh
echo "export JAVA_OPTS=\"-Xms256m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=128m -Djava.awt.headless=true\"">>$INSTALL_DIR/start.sh
echo "cd $INSTALL_DIR/tomcat6/">>$INSTALL_DIR/start.sh
echo "bin/startup.sh 2> \&1 >/dev/null">>$INSTALL_DIR/start.sh
echo "echo \"Tomcat started.\"">>$INSTALL_DIR/start.sh
echo "cd $INSTALL_DIR/agent-standalone/">>$INSTALL_DIR/start.sh
echo "echo \"Starting Agent...\"">>$INSTALL_DIR/start.sh
echo "./run.sh 2> \&1 >/dev/null">>$INSTALL_DIR/start.sh
echo "echo \"Agent started.\"">>$INSTALL_DIR/start.sh
chmod 755 $INSTALL_DIR/start.sh 2>/dev/null

echo "Creating stop script at $INSTALL_DIR/stop.sh..."
echo "#!/bin/bash">$INSTALL_DIR/stop.sh
echo "echo \"Stopping Tomcat...\"">>$INSTALL_DIR/stop.sh
echo "$INSTALL_DIR/tomcat6/bin/shutdown.sh 2> \&1 >/dev/null">>$INSTALL_DIR/stop.sh
echo "echo \"Stopping Agent...\"">>$INSTALL_DIR/stop.sh
echo "kill \$(ps aux | grep '[a]gent-standalone' | awk '{print \$2}') 2> \&1 >/dev/null">>$INSTALL_DIR/stop.sh
echo "kill \$(ps aux | grep '[a]piharness' | awk '{print \$2}') 2> \&1 >/dev/null">>$INSTALL_DIR/stop.sh
echo "kill \$(ps aux | grep '[t]omcat' | awk '{print \$2}') 2> \&1 >/dev/null">>$INSTALL_DIR/stop.sh
echo "echo \"Tomcat and Agents stopped.\"">>$INSTALL_DIR/stop.sh
chmod 755 $INSTALL_DIR/stop.sh 2>/dev/null

echo "#!/bin/bash">$INSTALL_DIR/agent-standalone/run.sh
echo "controller=\"http://localhost:8080\"">>$INSTALL_DIR/agent-standalone/run.sh
echo "host=\`hostname\`">>$INSTALL_DIR/agent-standalone/run.sh
echo "capacity=400">>$INSTALL_DIR/agent-standalone/run.sh
echo "nohup java -jar agent-standalone-all.jar -controller=\$controller -host=\$host -capacity=\$capacity &">>$INSTALL_DIR/agent-standalone/run.sh



echo "Installing all in one in $INSTALL_DIR"
echo "Run $INSTALL_DIR/start.sh to start the all in one server and $INSTALL_DIR/stop.sh to kill it"