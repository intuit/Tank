#!/bin/bash
# Installs a local, single-machine Tank: a standalone controller on Tomcat with an H2 database, and one
# standalone agent. For trying Tank out and small tests only, not for production or large-scale load.
#
# Usage: all-in-one.sh [install-dir]          (default: the current directory)
#
# It installs the controller war and agent package built from this source tree, so build them first:
#   mvn clean install -P release
# or point it at other copies, for example ones downloaded from a release:
#   TANK_WAR=/path/to/tank.war TANK_AGENT_ZIP=/path/to/agent-standalone-pkg.zip ./all-in-one.sh
#
# Needs Java 21 or later on the PATH (or JAVA_HOME), curl and unzip.
set -euo pipefail

TOMCAT_VERSION=11.0.27
H2_VERSION=2.3.232
SOURCE_DIR="$(cd "$(dirname "$0")" && pwd)"
TANK_WAR="${TANK_WAR:-$SOURCE_DIR/web/web_ui/target/tank.war}"
TANK_AGENT_ZIP="${TANK_AGENT_ZIP:-$SOURCE_DIR/agent/agent_standalone_pkg/target/agent-standalone-pkg.zip}"
TANK_SETTINGS="${TANK_SETTINGS:-$SOURCE_DIR/assets/settings-all-in-one.xml}"

fail() {
    echo "all-in-one: $*" >&2
    exit 1
}

for tool in curl unzip java; do
    command -v "$tool" >/dev/null || fail "$tool is required"
done
java_major=$(java -version 2>&1 | awk -F'"' '/version/ {split($2, v, "."); print v[1]; exit}')
[ "${java_major:-0}" -ge 21 ] 2>/dev/null || fail "Java 21 or later is required; java -version reports ${java_major:-unknown}"
for artifact in "$TANK_WAR" "$TANK_AGENT_ZIP" "$TANK_SETTINGS"; do
    [ -f "$artifact" ] || fail "$artifact not found. Build with 'mvn clean install -P release', or set TANK_WAR, TANK_AGENT_ZIP and TANK_SETTINGS"
done
# agents download the test harness from the war's tools folder, which only a release build has
unzip -l "$TANK_WAR" tools/apiharness-1.0-all.jar >/dev/null 2>&1 \
    || fail "$TANK_WAR has no tools/apiharness-1.0-all.jar; build it with 'mvn clean install -P release'"

INSTALL_DIR="${1:-$(pwd)}"
mkdir -p "$INSTALL_DIR"
INSTALL_DIR="$(cd "$INSTALL_DIR" && pwd)"
TOMCAT_DIR="$INSTALL_DIR/tomcat"
AGENT_DIR="$INSTALL_DIR/agent-standalone"
[ -e "$TOMCAT_DIR" ] && fail "$TOMCAT_DIR already exists; run $INSTALL_DIR/stop.sh and remove it, or choose another directory"
echo "Installing Tank all-in-one in $INSTALL_DIR"

echo "Downloading Tomcat $TOMCAT_VERSION..."
curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-11/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz" \
    | tar -xz -C "$INSTALL_DIR"
ln -snf "$INSTALL_DIR/apache-tomcat-$TOMCAT_VERSION" "$TOMCAT_DIR"
rm -rf "$TOMCAT_DIR"/webapps/*
mkdir -p "$TOMCAT_DIR/jars" "$INSTALL_DIR/data"

echo "Downloading the H2 database driver..."
curl -fsSL -o "$TOMCAT_DIR/lib/h2-$H2_VERSION.jar" \
    "https://repo1.maven.org/maven2/com/h2database/h2/$H2_VERSION/h2-$H2_VERSION.jar"

echo "Installing the controller..."
cp "$TANK_WAR" "$TOMCAT_DIR/webapps/ROOT.war"
agent_token=$(od -An -tx1 -N24 /dev/urandom | tr -d ' \n')
sed "s/@AGENT_TOKEN@/$agent_token/" "$TANK_SETTINGS" > "$TOMCAT_DIR/conf/settings.xml"
# the database is a file under data/, so projects and scripts survive restarts
cat > "$TOMCAT_DIR/conf/context.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/watsDS" auth="Container"
        type="javax.sql.DataSource"
        factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
        driverClassName="org.h2.Driver"
        url="jdbc:h2:file:$INSTALL_DIR/data/tank;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=USER,VALUE,END"
        username="sa" password=""
        maxActive="20" maxIdle="10" maxWait="10000"/>
</Context>
EOF

echo "Installing the agent..."
unzip -q -o "$TANK_AGENT_ZIP" -d "$INSTALL_DIR"
cat > "$AGENT_DIR/run.sh" <<EOF
#!/bin/bash
cd "\$(dirname "\$0")"
nohup java -jar agent-standalone-all.jar -controller=http://localhost:8080 -token=$agent_token \\
    -host=localhost -capacity=400 > agent.out 2>&1 &
echo \$! > agent.pid
EOF
chmod 700 "$AGENT_DIR/run.sh"
chmod 600 "$TOMCAT_DIR/conf/settings.xml"

cat > "$INSTALL_DIR/start.sh" <<EOF
#!/bin/bash
# Starts the Tank controller and agent installed by all-in-one.sh
export WATS_PROPERTIES="$TOMCAT_DIR/conf"
# persistence.xml is written for MySQL; this install uses H2
export CATALINA_OPTS="-Xms256m -Xmx2g -Djava.awt.headless=true -Dhibernate.dialect=org.hibernate.dialect.H2Dialect"
cd "$TOMCAT_DIR"
echo "Starting the controller..."
bin/startup.sh > /dev/null
echo "Waiting for http://localhost:8080 ..."
for i in \$(seq 1 120); do
    curl -fs -o /dev/null http://localhost:8080/v2/ping && break
    sleep 2
done
curl -fs -o /dev/null http://localhost:8080/v2/ping || { echo "The controller did not start; see $TOMCAT_DIR/logs"; exit 1; }
echo "Starting the agent..."
"$AGENT_DIR/run.sh"
echo "Tank is running at http://localhost:8080 (user admin, password admin)"
EOF

cat > "$INSTALL_DIR/stop.sh" <<EOF
#!/bin/bash
# Stops the Tank controller and agent installed by all-in-one.sh
echo "Stopping the agent..."
if [ -f "$AGENT_DIR/agent.pid" ]; then
    kill "\$(cat "$AGENT_DIR/agent.pid")" 2>/dev/null || true
    rm -f "$AGENT_DIR/agent.pid"
fi
pkill -f "com.intuit.tank.harness.APITestHarness" 2>/dev/null || true
echo "Stopping the controller..."
"$TOMCAT_DIR/bin/shutdown.sh" > /dev/null 2>&1 || true
echo "Stopped."
EOF
chmod 755 "$INSTALL_DIR/start.sh" "$INSTALL_DIR/stop.sh"

echo
echo "Installed. Run $INSTALL_DIR/start.sh to start Tank and $INSTALL_DIR/stop.sh to stop it."
