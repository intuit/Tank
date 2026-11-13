# Installation

This page covers a production-style install in AWS: the AWS resources, the controller on Tomcat, and the agent image the controller launches. To try Tank on one machine first, see the [Quickstart](quickstart.md). Every setting mentioned here is described in [Configuration](configuration.md).

## Requirements

| Component | Version |
|-----------|---------|
| Java | 21 or later. The build targets Java 21 (`maven.compiler.release`); the project badge lists 21 and 25. |
| Apache Tomcat | 11.0 (the build compiles against Tomcat 11.0.x). The project badge also lists 10.1. |
| MySQL | 8.x. The CloudFormation template creates RDS MySQL 8.4. The war bundles MySQL Connector/J (`com.mysql.cj.jdbc.Driver`). |
| Maven | Only to build from source. The build downloads its own Node.js to build the React UI. |

### Choosing a JDK

Use an OpenJDK distribution approved by your organization, on both the controller and the agents, at the Java major version above. Don't select a JDK by version alone: point `JAVA_HOME` at the exact installation you intend to use.

=== "macOS"

    ```bash
    # List installed JDKs and their paths
    /usr/libexec/java_home -V

    # Set JAVA_HOME to the exact JDK you chose
    export JAVA_HOME=<path-to-intended-jdk>/Contents/Home
    export PATH="$JAVA_HOME/bin:$PATH"
    ```

=== "Linux"

    ```bash
    export JAVA_HOME=<path-to-intended-jdk>
    export PATH="$JAVA_HOME/bin:$PATH"
    ```

Replace `<path-to-intended-jdk>` with the install directory of the JDK you chose. Then check it with `java -version`: the first line shows the major version (it must be 21 or later) and the runtime line names the vendor's build. Confirm both are what you intended before you build or deploy.

## Build Tank

```bash
export MAVEN_OPTS="$MAVEN_OPTS -Xms2g -Xmx3g"
mvn clean install -P release
```

The `release` profile also builds the agent packages and tools. A plain `mvn clean install` builds the war but not the agent zips.

| Artifact | Path | Used for |
|----------|------|----------|
| Controller | `web/web_ui/target/tank.war` | Deploy to Tomcat. |
| Agent startup package | `agent/agent_startup_pkg/target/agent-startup-pkg.zip` | Baked into the agent AMI. |
| Standalone agent package | `agent/agent_standalone_pkg/target/agent-standalone-pkg.zip` | [Standalone agents](#standalone-deployment). |
| Debugger, script filter, proxy | See [Supplemental tools](../supplemental-tools.md). | Desktop tools. |

## AWS resources

Tank uses these AWS services:

- **EC2**: agent instances, AMIs, security groups, key pairs and, optionally, Elastic IPs bound to agents so the system under test can allow-list them.
- **RDS for MySQL** (or any MySQL 8 server you run and back up yourself).
- **IAM**: instance profiles for the controller and the agents.
- **CloudWatch**: test results (the default results store), agent logs and host metrics.
- **S3**: optional storage for data files and agent support files, and optional results storage.
- **SSM Parameter Store**: optional, for the agent AMI ID and the OIDC client secret.
- **X-Ray**: optional tracing of the controller.

The `aws-config/` folder in the repository has CloudFormation templates for most of this:

| Template | Creates |
|----------|---------|
| `tank_instanceProfiles.yml` | The controller and agent IAM roles and instance profiles. It exports `TankControllerProfile`, which the controller template imports. Deploy it first. |
| `tank_controller.yml` | An RDS MySQL 8.4 instance, security groups, and a Graviton (arm64) Amazon Linux 2023 controller with Tomcat 11, the CloudWatch agent and the AWS OpenTelemetry Java agent. It takes the S3 locations of `tank.war` and `settings.xml` as parameters. |
| `tank_agent.yml` | A Graviton (arm64) Amazon Linux 2023 instance with the agent installed as a `systemd` service, ready to snapshot into an agent AMI. It takes the S3 location of `agent-startup-pkg.zip`. |

!!! warning "Review the controller template before you use it"
    `tank_controller.yml` copies `settings.xml` to `/usr/share/tomcat/settings.xml` and the war to `/usr/share/tomcat/webapps/ROOT.war`, but it runs Tomcat from `/opt/tomcat` (working directory `/opt/tomcat`) and doesn't set `WATS_PROPERTIES`. As written, Tomcat won't find either file. Change those paths, or set `WATS_PROPERTIES`, before you rely on it. The templates also install Amazon Corretto 25 (`java-25-amazon-corretto-headless`); change the package if your organization requires a different OpenJDK distribution.

### Key pair

Create an EC2 key pair in each region you will run agents in, and keep the private key: AWS can't give it back to you. The key pair name goes in the `keypair` setting.

### Security groups

| Group | Inbound rules |
|-------|---------------|
| Controller | TCP 8080 (Tomcat) from your users and from the agents; SSH 22 from your admin network. |
| Agents | The agent port, TCP 8090 by default (`agent-port`), from the controller. The controller calls agents over HTTP and WebSocket on this port. |
| Database | TCP 3306 from the controller. |

Agents also need outbound access to the controller's `controller-base-url` and to the systems under test.

### IAM

Prefer instance profiles to access keys. Without keys in `settings.xml`, Tank uses the AWS SDK's default credential chain, which picks up the instance profile.

`tank_instanceProfiles.yml` grants:

- **Controller role**: the managed policies `AmazonEC2FullAccess`, `AmazonS3FullAccess`, `AWSXrayWriteOnlyAccess` and `CloudWatchAgentServerPolicy`, plus `iam:PassRole`, `iam:ListInstanceProfiles`, `iam:AddRoleToInstanceProfile`, `appconfig:GetConfiguration`, `cloudwatch:GetMetricData` and `cloudwatch:ListMetrics`. The controller needs `iam:PassRole` to launch agents with the agent instance profile, and `cloudwatch:GetMetricData` to show agent metrics.
- **Agent role**: `AmazonS3FullAccess` and `CloudWatchAgentServerPolicy`, plus `cloudwatch:PutMetricData` and `cloudwatch:PutMetricStream`.

!!! note
    The template's controller role has no `ssm:GetParameter` permission. Add it if you use `ssm-ami` for the agent AMI or an SSM parameter for the OIDC client secret. You can also narrow the managed policies to the buckets and actions you use.

If you can't use instance profiles, set access keys through the `credentials` element (see [Configuration](configuration.md#credentials)). The controller passes those keys to every agent in the EC2 user data.

### Database

Create an empty database for Tank (the templates name it `tank`) and a user that can create and alter tables in it. Hibernate creates and updates the schema when the controller starts. Back the database up: it holds all your projects and scripts.

## Agent AMI

Agents run from an AMI you build once per region. The default instance types in `settings.xml` are Graviton (`m7g`, `m8g`), so build an **arm64** AMI unless you change the instance types.

=== "CloudFormation"

    1. Upload `agent-startup-pkg.zip` to S3.
    2. Create a stack from `aws-config/tank_agent.yml`, passing the zip's S3 location as `TankZip`, a key pair, subnets and security groups.
    3. When the stack completes, create an AMI from the instance, then delete the stack.

=== "Manual"

    On an arm64 Linux instance:

    1. Install an approved OpenJDK (Java 21 or later) and `unzip`.
    2. Unzip the package into `/opt`, which creates `/opt/tank_agent`, and create its log folder:

        ```bash
        sudo unzip agent-startup-pkg.zip -d /opt
        sudo mkdir /opt/tank_agent/logs
        ```

    3. Start `/opt/tank_agent/run.sh` at boot from `/opt/tank_agent`. The template's unit file:

        ```ini
        [Unit]
        Description=Tank agent
        After=network-online.target
        Wants=network-online.target

        [Service]
        Type=simple
        WorkingDirectory=/opt/tank_agent
        ExecStart=/opt/tank_agent/run.sh
        LimitNOFILE=20000
        UMask=0077

        [Install]
        WantedBy=multi-user.target
        ```

    4. Raise the open-file limits and the ephemeral port range for high connection counts. The template sets `DefaultLimitNOFILE=81960` for `systemd` and adds `net.ipv4.ip_local_port_range=1024 65000` to `/etc/sysctl.conf`.
    5. Optionally install the CloudWatch agent to ship `/opt/tank_agent/logs/agent.log` and host metrics (see the template for the configuration).
    6. Create an AMI from the instance.

Put the AMI ID in the `ami` setting of each region's `instance-description`, or publish it as an SSM parameter and use `ssm-ami`.

When a test starts, the controller launches instances from this AMI with the controller URL, agent token and job details in the user data. The agent fetches its settings and support files from the controller and then starts the test harness.

## Controller

=== "CloudFormation"

    1. Deploy `tank_instanceProfiles.yml`.
    2. Upload `tank.war` and your `settings.xml` to S3.
    3. Deploy `tank_controller.yml` with those S3 locations, a VPC, subnets, a key pair and an RDS password. Read the warning under [AWS resources](#aws-resources) first.

=== "Manual"

    1. Install an approved OpenJDK (Java 21 or later) and set `JAVA_HOME` [as above](#choosing-a-jdk).
    2. Download Tomcat 11 from [tomcat.apache.org](https://tomcat.apache.org/) and unpack it, for example to `/opt/tomcat`.
    3. Remove the default applications and deploy Tank at the root context:

        ```bash
        rm -rf /opt/tomcat/webapps/*
        cp web/web_ui/target/tank.war /opt/tomcat/webapps/ROOT.war
        ```

    4. Define the `jdbc/watsDS` data source in `/opt/tomcat/conf/context.xml`. This is the definition the controller template uses:

        ```xml
        <?xml version="1.0" encoding="UTF-8"?>
        <Context>
            <Resource name="jdbc/watsDS" auth="Container"
                type="javax.sql.DataSource"
                factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
                maxWait="10000"
                username="admin" password="REPLACE_ME"
                testOnBorrow="true"
                validationQuery="SELECT 1"
                removeAbandonedOnBorrow="true"
                removeAbandonedOnMaintenance="true"
                removeAbandonedTimeout="60"
                driverClassName="com.mysql.cj.jdbc.Driver"
                url="jdbc:mysql://DB_HOST:3306/tank"/>
        </Context>
        ```

    5. Tell Tank where its settings file lives. Tank reads `settings.xml` from Tomcat's working directory, or from the folder in the `WATS_PROPERTIES` environment variable or Java system property when the working directory has none. For example, in `/opt/tomcat/bin/setenv.sh`:

        ```bash
        export WATS_PROPERTIES=/opt/tank/conf
        ```

    6. Start Tomcat. If no settings file exists, Tank writes the default one to that location. Stop Tomcat, edit the file (at least `controller-base-url`, `instance-name`, the agent AMIs, the subnets and security groups, and the default admin password), and start Tomcat again.

Then open `http://<controller-host>:8080/` and sign in. On first start Tank creates the users listed under `security/users` in `settings.xml`; the default file creates `admin` with the password `admin`. Change that password straight away, or change the entry before the first start.

!!! tip "Keep secrets away from agents"
    Agents download the controller's `settings.xml`. If a file named `agent-settings.xml` exists in the same folder, agents get that file instead, so you can keep credentials and SSO settings out of what agents receive.

### Upgrading

Build the new war, replace `ROOT.war` and restart Tomcat. Tank updates the database schema on startup.

Agents keep running while the controller restarts. With `command-ws-enabled` set to `true` and `agent-readoption-enabled` set to a number of seconds, the restarted controller finds running agents by their `Controller` and `JobId` EC2 tags and reconnects to them.

## Standalone deployment

In standalone mode the controller doesn't start EC2 instances. Instead, standalone agents that you run yourself register with the controller and wait for work. Use it for small tests or where you can't use EC2.

1. Install the controller and database as above.
2. In `settings.xml`, set `<standalone>true</standalone>`. For small tests you can also send results straight to the controller with the REST reporter, as the all-in-one settings file (`assets/settings-all-in-one.xml`) does:

    ```xml
    <reporting>
      <provider>
        <reporter>com.intuit.tank.reporting.rest.RestResultsReporter</reporter>
        <reader>com.intuit.tank.reporting.rest.RestResultsReader</reader>
      </provider>
    </reporting>
    ```

    The REST reporter sends every result through the controller, so don't use it for large tests.

3. Set an `agent-token` in `settings.xml`. Standalone agents need a token to start.
4. On each agent machine, unzip `agent-standalone-pkg.zip` and start the agent:

    ```bash
    java -jar agent-standalone-all.jar \
      -controller=http://<controller-host>:8080 \
      -token=<agent-token> \
      -host=<this-machine's-address> \
      -capacity=4000
    ```

    | Option | Meaning |
    |--------|---------|
    | `-controller` | Required. The controller's base URL. |
    | `-token` | Required. The `agent-token` from `settings.xml`, or a user API token. |
    | `-host` | The address the controller uses to reach this agent. Optional if the agent can work it out. |
    | `-capacity` | The number of users this agent can run. Defaults to 4000. |

The agent listens on TCP 1090 for the controller, so open that port to the controller. It reports its availability every five minutes. When it gets a job, it downloads the settings and support files into `/opt/tank_agent` (create that folder and make it writable by the agent's user) and runs `./startAgent.sh` from its working directory.

When you start a test, the controller picks available standalone agents with enough total capacity for the test's users. If there isn't enough capacity, the job is aborted.

!!! warning "Standalone package not verified end to end"
    The `run.sh` and `README.txt` in the standalone package predate the required `-token` option, so `run.sh` as shipped exits with a usage message. The package also ships `start_wats.sh` rather than the `startAgent.sh` that the agent runs. Check both before you rely on standalone agents.
