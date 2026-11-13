# Quickstart

This page gets Tank running on your own machine so you can explore the UI, record and edit scripts, build projects and run small tests. It's for trying Tank out, not for large or production tests. For a full install, see [Installation](installation.md).

There are two ways to do it:

- **`all-in-one.sh`**: one script installs a controller with an H2 database and a standalone agent, ready to run tests. Start here.
- **Tomcat and MySQL by hand**: closer to a real deployment, and the database is MySQL as in production.

## Prerequisites

- An OpenJDK distribution approved by your organization, Java 21 or later. Set `JAVA_HOME` to it explicitly and check it with `java -version`, as described in [Choosing a JDK](installation.md#choosing-a-jdk).
- Maven, to build Tank.
- `curl` and `unzip`.
- For the MySQL path only: Docker and [Apache Tomcat 11](https://tomcat.apache.org/).

## The all-in-one install

Build Tank with the `release` profile, which packages the agent and the test harness the agent downloads, then run the script with the directory to install into:

```bash
export MAVEN_OPTS="$MAVEN_OPTS -Xms2g -Xmx3g"
mvn clean install -P release -DskipTests
./all-in-one.sh ~/tank-aio
~/tank-aio/start.sh
```

`all-in-one.sh` downloads Tomcat 11 and the H2 database driver, deploys the war you just built as `ROOT.war`, writes `settings.xml` from `assets/settings-all-in-one.xml` with a generated agent token, and unpacks a standalone agent that registers with the controller. `start.sh` waits until the controller answers, then starts the agent. Open `http://localhost:8080/` and sign in as `admin` / `admin`.

To use copies of the war and agent package from somewhere else, set `TANK_WAR` and `TANK_AGENT_ZIP` (and optionally `TANK_SETTINGS`) before running the script.

Stop everything with `~/tank-aio/stop.sh`. The database is a file under `~/tank-aio/data`, so your scripts and projects are still there after a restart. To start over, stop Tank and delete the directory.

!!! note "Limits of the all-in-one install"
    - REST security is off (`rest-security-enabled` is `false`), so anyone who can reach port 8080 has full rights. Keep it on your own machine.
    - Tank's results stores are external services (CloudWatch, S3, DynamoDB, Wavefront or Graphite; see [Configuration](configuration.md)). Without one, a test runs and reports its users and status, but no response times are recorded.
    - H2 is only for trying Tank out. Use MySQL for anything you keep.

## Tomcat and MySQL by hand

### 1. Build the war

From the root of the repository:

```bash
export MAVEN_OPTS="$MAVEN_OPTS -Xms2g -Xmx3g"
mvn clean install -DskipTests
```

The controller is `web/web_ui/target/tank.war`. Add `-P release` if you also want the agent packages and tools.

### 2. Start MySQL

```bash
docker run -d --name tank-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=tank \
  -e MYSQL_DATABASE=tank \
  -e MYSQL_USER=tank \
  -e MYSQL_PASSWORD=tank \
  mysql:8.4
```

You don't need to load a schema: Tank creates its tables when it starts.

### 3. Install Tomcat and Tank

Unpack Tomcat 11 somewhere, for example `~/tank/tomcat`, then deploy Tank at the root context:

```bash
cd ~/tank/tomcat
rm -rf webapps/*
cp /path/to/Tank/web/web_ui/target/tank.war webapps/ROOT.war
```

Replace `conf/context.xml` with a data source for the database:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource name="jdbc/watsDS" auth="Container"
        type="javax.sql.DataSource"
        factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
        maxWait="10000"
        username="tank" password="tank"
        testOnBorrow="true"
        validationQuery="SELECT 1"
        driverClassName="com.mysql.cj.jdbc.Driver"
        url="jdbc:mysql://localhost:3306/tank"/>
</Context>
```

Tell Tank where to keep its settings file by creating `bin/setenv.sh`:

```bash
export WATS_PROPERTIES="$HOME/tank/conf"
```

### 4. Start, then configure

```bash
bin/catalina.sh run
```

On first start Tank writes a default `settings.xml` to `~/tank/conf`, creates its tables, and creates the user `admin` with the password `admin`. Stop Tomcat (Ctrl+C) and edit `~/tank/conf/settings.xml` for local use:

```xml
<controller-base-url>http://localhost:8080</controller-base-url>
<standalone>true</standalone>
```

Inside `<agent-config>`, set a token for agents:

```xml
<agent-token>choose-a-long-random-string</agent-token>
```

Inside `<vm-manager>`, turn off CloudWatch agent metrics, which need AWS credentials:

```xml
<agent-metrics>
  <enabled>false</enabled>
</agent-metrics>
```

The default `<reporting>` section sends results to CloudWatch, which needs AWS credentials. Without them a test still runs, but records no response times; see [Configuration](configuration.md) for the other results stores.

Start Tomcat again and open:

- `http://localhost:8080/`: the web UI. Sign in as `admin` / `admin`, then change the password.
- `http://localhost:8080/classic/`: the classic JSF UI.

See [Configuration](configuration.md) for everything else in the file.

### 5. Run a test

In standalone mode the controller doesn't start agents in AWS: it uses standalone agents that register with it. See [Standalone deployment](installation.md#standalone-deployment) for how to start one.

To check a script or project without any agents, use the Tank debugger (`Tank-Debugger-all.jar`, built by `-P release`). See [Supplemental tools](../supplemental-tools.md).

To run tests from EC2 agents instead, set `<standalone>false</standalone>`, give the controller AWS credentials, and complete the agent setup in [Installation](installation.md#agent-ami).

## The docker-experimental folder

!!! warning "`docker-experimental/` doesn't work with current Tank"
    The Compose file starts MySQL 5.7; Tank's deployment template uses MySQL 8.4. `setup.sh` copies a schema into a container named `docker_db_1`, which only matches if you run Compose from a folder named `docker`, and the schema dump is years older than the current data model. The bundled `context.xml` and MySQL driver are for Tomcat 8.5. Use one of the two paths above instead.
