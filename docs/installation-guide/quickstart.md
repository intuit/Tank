# Quickstart

This page gets a Tank controller running on your own machine, with MySQL in Docker, so you can explore the UI, record and edit scripts, and build projects. It's for trying Tank out, not for large or production tests. For a full install, see [Installation](installation.md).

## Prerequisites

- An OpenJDK distribution approved by your organization, Java 21 or later. Set `JAVA_HOME` to it explicitly and check it with `java -version`, as described in [Choosing a JDK](installation.md#choosing-a-jdk).
- Maven, to build Tank.
- Docker, for MySQL.
- [Apache Tomcat 11](https://tomcat.apache.org/).

## 1. Build the war

From the root of the repository:

```bash
export MAVEN_OPTS="$MAVEN_OPTS -Xms2g -Xmx3g"
mvn clean install -DskipTests
```

The controller is `web/web_ui/target/tank.war`. Add `-P release` if you also want the agent packages and tools.

## 2. Start MySQL

```bash
docker run -d --name tank-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=tank \
  -e MYSQL_DATABASE=tank \
  -e MYSQL_USER=tank \
  -e MYSQL_PASSWORD=tank \
  mysql:8.4
```

You don't need to load a schema: Tank creates its tables when it starts.

## 3. Install Tomcat and Tank

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

## 4. Start, then configure

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

Then replace the `<reporting>` section so results go straight to the controller instead of CloudWatch:

```xml
<reporting>
  <provider>
    <reporter>com.intuit.tank.reporting.rest.RestResultsReporter</reporter>
    <reader>com.intuit.tank.reporting.rest.RestResultsReader</reader>
  </provider>
</reporting>
```

Start Tomcat again and open:

- `http://localhost:8080/`: the web UI. Sign in as `admin` / `admin`, then change the password.
- `http://localhost:8080/classic/`: the classic JSF UI.

See [Configuration](configuration.md) for everything else in the file.

## 5. Run a test

In standalone mode the controller doesn't start agents in AWS: it uses standalone agents that register with it. See [Standalone deployment](installation.md#standalone-deployment) for how to start one, and read the warning there first.

To check a script or project without any agents, use the Tank debugger (`Tank-Debugger-all.jar`, built by `-P release`). See [Supplemental tools](../supplemental-tools.md).

To run tests from EC2 agents instead, set `<standalone>false</standalone>`, give the controller AWS credentials, and complete the agent setup in [Installation](installation.md#agent-ami).

## Older quickstart scripts

The repository has two older quickstart paths. Neither works as shipped with current Tank:

!!! warning "`all-in-one.sh`"
    The script downloads Tank 4.2.0 artifacts, not the current release. It fetches the settings file from a GitHub page URL (`.../blob/...`), which returns HTML rather than XML. The `context.xml` it writes starts with a `#!/bin/bash` line before the XML declaration. Its `start.sh` sets `-XX:PermSize` and `-XX:MaxPermSize`, which current JVMs reject at startup. Its generated agent `run.sh` loses the controller URL, host and capacity values (they're expanded while the script runs) and passes no `-token`.

!!! warning "`docker-experimental/`"
    The Compose file starts MySQL 5.7; Tank's deployment template uses MySQL 8.4. `setup.sh` copies a schema into a container named `docker_db_1`, which only matches if you run Compose from a folder named `docker`, and the schema dump is years older than the current data model. The bundled `context.xml` and MySQL driver are for Tomcat 8.5. Hibernate creates the tables itself, so you don't need the schema; use the steps above instead.
