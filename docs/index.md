# Intuit Tank

Intuit Tank is an open-source load test platform. You record or write scripts, group them into projects, and Tank runs them from on-demand agents, usually EC2 instances in AWS. It reports response times and throughput while the test runs.

Tank is built for load testing. You can use it for functional checks, but it lacks many of the tools a functional test suite usually needs.

## Components

Tank has two parts: a **controller** and one or more **agents**.

### Controller

The controller is the control plane. It is a single web application, `tank.war`, that runs on Apache Tomcat and serves:

| Path | What it is |
|------|------------|
| `/` | The web UI (React). |
| `/classic/` | The original JSF/PrimeFaces UI. It moved here from the root in Tank 5.0.0. |
| `/v2/...` | The REST API (Spring MVC). See [Using the REST API](api/index.md). |

The controller stores projects, scripts, filters, data file metadata, jobs and users in MySQL. It starts agents, sends them their configuration and test files, and tracks them while a test runs.

### Agents

Agents generate the load. The controller starts them on demand for a test and terminates them when the test ends, so you pay only for the time the test runs. An agent receives the settings file, the test script and any support files from the controller, and then reports its status and results back.

You can also run long-lived **standalone agents** on machines you manage, with the controller set not to start EC2 instances. See [Standalone deployment](installation-guide/installation.md#standalone-deployment).

## Architecture

```text
   Browser / REST client
            │  HTTP(S) :8080
            ▼
 ┌─────────────────────────┐        JDBC (jdbc/watsDS)     ┌─────────┐
 │ Controller (tank.war)   │──────────────────────────────▶│  MySQL  │
 │ Tomcat 11               │                               └─────────┘
 │  /         React UI     │──── data files, agent jars ──▶ S3 or local disk
 │  /classic  JSF UI       │──── EC2 RunInstances ───────▶ agent instances
 │  /v2       REST API     │◀─── CloudWatch metrics ────── (Agent Tracker, results)
 └─────────┬───────────────┘
           │ HTTP + WebSocket to agent-port (default 8090)
           ▼                      ▲ REST calls to /v2/agent/... (settings, status, results)
 ┌─────────────────────────┐      │
 │ Agents (EC2 or standalone)────┘
 └─────────────────────────┘
```

| Piece | What Tank uses it for |
|-------|----------------------|
| **Java** | The build targets Java 21 (`maven.compiler.release` in the root `pom.xml`). The project badge lists Java 21 and 25, and the CloudFormation templates in `aws-config/` install a Java 25 runtime. |
| **Apache Tomcat** | Hosts the controller. The build compiles against Tomcat 11.0.x, and the `aws-config/` template installs Tomcat 11. The project badge also lists Tomcat 10.1. |
| **MySQL** | The controller's database, reached through the JNDI data source `jdbc/watsDS`. Hibernate creates and updates the tables on startup and keeps audit history (Envers) in `*_version` tables. The CloudFormation template creates an RDS MySQL 8.4 instance. |
| **Amazon EC2** | Agent instances, started with the AWS SDK for Java v2. Tank tags each agent with `Name`, `Controller` (the controller's `instance-name`), `JobId` and `JobName`, plus any tags you configure. Elastic IPs can be bound to agents. |
| **Amazon S3** | Optional storage for data files and agent support files (any storage setting that starts with `s3:`), and an optional results store. |
| **Amazon CloudWatch** | The default results store: response-time and count metrics go to the `Intuit/Tank` namespace. The controller also reads the agents' CPU, memory and JVM metrics from CloudWatch for the Agent Tracker. The templates install the CloudWatch agent to ship logs and host metrics. |
| **AWS X-Ray** | Optional request tracing. The controller template runs Tomcat with the AWS Distro for OpenTelemetry Java agent. |
| **AWS SSM Parameter Store** | Optional: the agent AMI ID (`ssm-ami`) and the OIDC client secret can be read from parameters. |

Other results stores (DynamoDB, S3, Wavefront, Graphite) and an OIDC single sign-on provider can be configured in the [settings file](installation-guide/configuration.md).

!!! note "No message queue"
    Older guides describe ActiveMQ/JMS between the controller and agents. Current Tank has no message queue: the controller and agents talk over HTTP and WebSocket only.

## Where to go next

- [Quickstart](installation-guide/quickstart.md): run a controller on your own machine.
- [Installation](installation-guide/installation.md): set up AWS, the controller and agents.
- [Configuration](installation-guide/configuration.md): every key in `settings.xml`.
- [Key Concepts](key-concepts/projects.md): projects, scripts, filters, data files and functions.
- [Roles and permissions](roles.md) and [Administration](administration.md).
- [Supplemental tools](supplemental-tools.md): the recording proxy, debugger and script filter tools.
- [REST API](api/index.md): using the REST V2 API, and the [API reference](api/reference.md).
- [FAQ](faq.md)
