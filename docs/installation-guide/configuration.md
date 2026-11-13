# Configuration

The controller is configured by one XML file, `settings.xml`. This page lists every setting the code reads, with the value it uses when the setting is missing.

## Where the file lives

Tank looks for `settings.xml` in this order:

1. `settings.xml` in the controller's working directory (for Tomcat, the directory you start it from).
2. `$WATS_PROPERTIES/settings.xml`, where `WATS_PROPERTIES` is an environment variable or, if that isn't set, a Java system property (`-DWATS_PROPERTIES=/path`).

If the file doesn't exist, Tank writes its built-in default (`api/src/main/resources/settings.xml` in the source) to that location on first start, creating the folder if needed. Edit that copy, then restart the controller.

The root element is `<tank-settings>`. Each section below is independent and can appear in any order. Relative paths are resolved from the controller's working directory.

!!! tip "Settings for agents"
    Agents download the controller's settings file when they start, and the agent settings below are read from that copy. If a file named `agent-settings.xml` sits next to `settings.xml`, agents get it instead. Use it to keep credentials, SSO settings and other controller-only values off agent machines.

## Global settings

| Element | Default | Description |
|---------|---------|-------------|
| `instance-name` | `prod` | The name of this controller, without spaces. Tank tags each agent instance with it (`Controller` tag) and uses the tag to find its own agents after a restart. Give every controller sharing an AWS account a different name. |
| `controller-base-url` | `http://tank.controller/` | The URL agents use to reach the controller. Include the context path if Tank isn't deployed at the root (for example `http://host:8080/tank`). Agents receive it in their EC2 user data. |
| `standalone` | `false` | `true` means the controller doesn't start EC2 agents and uses registered standalone agents instead. See [Standalone deployment](installation.md#standalone-deployment). |
| `data-file-storage` | `/tmp/tank/datafiles` | Where uploaded data files are stored. A local path, or an S3 bucket written as `s3:bucket-name`. |
| `jar-file-storage` | `/tmp/tank/jars` | Files in this location are zipped and sent to every agent as support files, along with the test harness. A local path or `s3:bucket-name`. |
| `tmp-file-storage` | `/tmp/tank/tmpfiles` | Scratch space for generated scripts and the support-file zip. Must be a local path. |
| `s3-encrypt` | `false` | `true` to request server-side encryption when writing to S3 storage. |
| `rest-security-enabled` | `false` | `true` rejects `/v2` REST calls that carry no credentials (a session, a user API token or the agent token) with HTTP 401. When `false`, unauthenticated calls are allowed and permission checks pass for them. The login endpoints are always open. |
| `banner-text` | empty | A notice shown to users in the web UI. |
| `user-auto-deletion-enabled` | `false` | On startup, Tank looks for users who haven't logged in for the retention period. When `false` it only logs what it would do; when `true` it deletes (anonymizes) them. Can also be turned on with the system property `tank.user.auto-deletion.enabled` or the environment variable `TANK_USER_AUTO_DELETION_ENABLED`. |
| `user-auto-deletion-retention-days` | `730` | The inactivity period for user auto-deletion. The system property `tank.user.auto-deletion.retention-days` overrides it. |
| `user-auto-deletion-permitted-users` | empty | A comma-separated list of user names that auto-deletion never removes. |

### Products and locations

Lists the users can pick from in the UI. They're labels only.

```xml
<products>
  <product name="">All Products</product>
  <product name="MyProduct">My Product</product>
</products>

<locations>
  <location name="unspecified" displayName="Unspecified" />
  <location name="san_diego" displayName="San Diego, CA" />
</locations>
```

### Reporting

Chooses how agents report results and how the controller reads them back. Without this section Tank uses the database reporter.

```xml
<reporting>
  <provider>
    <reporter>com.intuit.tank.reporting.db.DatabaseResultsReporter</reporter>
    <reader>com.intuit.tank.reporting.db.DatabaseResultsReader</reader>
    <config></config>
  </provider>
</reporting>
```

| Reporter / reader | Use |
|-------------------|-----|
| `com.intuit.tank.reporting.db.DatabaseResultsReporter` / `...DatabaseResultsReader` | The default. Writes results to the store set by `vm-manager/results/provider` (CloudWatch in the default file). |
| `com.intuit.tank.reporting.rest.RestResultsReporter` / `...RestResultsReader` | Agents post results straight to the controller. Only for standalone or small tests: it overloads the controller under heavy load. |

`provider/config` is passed to the reporter as-is.

## Agent settings: `agent-config`

| Element | Default | Description |
|---------|---------|-------------|
| `agent-port` | `8090` | The port agents listen on for the controller's HTTP and WebSocket calls. Open it from the controller to the agents. |
| `agent-token` | empty | A shared secret that agents send as `Authorization: Bearer <token>`. A request with this token can do anything, so keep it secret. Set it when `rest-security-enabled` is `true`, and for standalone agents. |
| `agent-data-file-storage` | `/tmp` | Where agents store data files during a test. |
| `connection-timeout` | `40000` | HTTP connection timeout for test requests, in milliseconds. |
| `max-body-report-size` | `5000` | The most response-body characters logged for a failed request. |
| `valid-mime-types/mime-type-regex` | none | Regular expressions for the content types whose bodies are logged on error. The default file lists `.*text.*`, `.*json.*` and `.*xml.*`. |
| `log-post-response` | `false` | `true` logs the response to each POST request. |
| `max-response-time` | `5000` | If a response takes longer than this (ms), the agent waits extra time before the user's next request. |
| `max-failed-wait-time` | `180000` | The longest extra wait after a slow response (ms). Helps keep a struggling server from backing up. |
| `over-simulation-max-time` | `7200000` | How long a test may run past its planned simulation time (ms). |
| `status_report_interval_milis` | set by the caller | How often agents report status (ms). The default file sets `30000`. |
| `tps-period` | `15` | The period, in seconds, over which agents calculate transactions per second. The default file sets `30`. |
| `duration-simulation/simulation` | post 500–1000, get 50–300, process 10–50 | Assumed request times (ms), by step type, used to estimate how long a script takes: `<simulation for="get" min="50" max="300" />`. |
| `request-headers/header` | none | Headers added to every request: `<header key="name">value</header>`. |
| `tank-http-clients/tank-client` | Apache HttpClient 4.5, Apache HttpClient 5, JDK client | The HTTP clients users can choose for a test: `<tank-client name="Display name">class</tank-client>`. |
| `default-tank-client` | `Apache HttpClient 4.5` | The display name of the default client. The default file sets `JDK HttpClient`. |
| `command-ws-enabled` | `false` | `true` makes the controller send agent commands over a WebSocket connection to `agent-port` instead of plain HTTP calls. |
| `agent-readoption-enabled` | `0` | Seconds between passes in which the controller reconnects to running agents found by their `Controller` and `JobId` tags, for example after a controller restart. `0` turns it off; the default file suggests `300`. Needs `command-ws-enabled`; ignored in standalone mode. |

The HTTP client classes that ship with Tank are:

| Class | Client |
|-------|--------|
| `com.intuit.tank.httpclient4.TankHttpClient4` | Apache HttpClient 4.5 |
| `com.intuit.tank.httpclient5.TankHttpClient5` | Apache HttpClient 5 |
| `com.intuit.tank.httpclientjdk.TankHttpClientJDK` | The JDK's `java.net.http` client |

## Cloud settings: `vm-manager`

| Element | Default | Description |
|---------|---------|-------------|
| `default-region` | `US_EAST` | The default region for tests (a name from the region table below). |
| `use-agent-elastic-ips` | `false` | Default for binding available Elastic IPs to agents. |
| `reserved-elastic-ips/eip` | none | Elastic IPs that Tank must never bind to agents. |
| `tags/tag` | none | Extra tags for agent instances: `<tag name="admin_contact">tank_admin</tag>`. Tank adds `Name`, `Controller`, `JobId` and `JobName` itself. |

Regions use these names:

| Name | AWS region |
|------|------------|
| `US_EAST` | us-east-1 |
| `US_EAST_2` | us-east-2 |
| `US_WEST_1` | us-west-1 |
| `US_WEST_2` | us-west-2 |
| `EUROPE` | eu-west-1 |
| `ASIA_1` | ap-southeast-1 |
| `ASIA_2` | ap-southeast-2 |
| `ASIA_3` | ap-northeast-1 |
| `SOUTH_AMERICA` | sa-east-1 |

### Credentials

```xml
<credentials type="amazon">
  <!-- <secret-key-id>...</secret-key-id> <secret-key>...</secret-key> -->
  <!-- <proxy-host></proxy-host> <proxy-port>80</proxy-port> -->
</credentials>
```

Keep this element, even empty: the controller expects it when it launches agents. With no keys, Tank uses the AWS SDK's default credential chain (an instance profile on EC2), which is the recommended setup.

| Element | Description |
|---------|-------------|
| `secret-key-id-property` | The name of a Java system property or environment variable that holds the access key ID. Defaults to `AWS_SECRET_KEY_ID`. |
| `secret-key-property` | The same for the secret access key. Defaults to `AWS_SECRET_KEY`. |
| `secret-key-id`, `secret-key` | Keys written into the file, used only when the property or variable above is empty. |
| `proxy-host`, `proxy-port` | A proxy for the controller's EC2 API calls. |

!!! warning
    If access keys are configured by any of these means, the controller passes them to every agent in the EC2 user data.

### Instance descriptions

Each region you can run agents in needs an `instance-descriptions` block with an `AGENT` description. Only regions with one appear in the UI. Values in `default-instance-description` apply to every region unless the region sets its own.

```xml
<default-instance-description>
  <security-group>security_group</security-group>
  <keypair>myKey</keypair>
</default-instance-description>

<instance-descriptions region="US_WEST_2">
  <instance-description name="AGENT">
    <ami>[AMI-ID]</ami>
    <keypair>[KEYPAIR]</keypair>
    <iam-role>[INSTANCE-PROFILE-NAME]</iam-role>
    <vpc-subnet>[SUBNET-1]</vpc-subnet>
    <vpc-subnet>[SUBNET-2]</vpc-subnet>
    <security-group-ids>[SG-ID]</security-group-ids>
  </instance-description>
</instance-descriptions>
```

| Element | Description |
|---------|-------------|
| `ami` | The agent AMI ID. |
| `ssm-ami` | The name of an SSM parameter holding the AMI ID. Used in preference to `ami`, which is the fallback if the lookup fails. The controller needs `ssm:GetParameter`. |
| `keypair` | The EC2 key pair name. |
| `iam-role` | The **instance profile** name to launch agents with. Recommended over access keys. |
| `vpc-subnet` | A subnet to launch agents in. Repeat it to spread agents over several subnets. |
| `security-group-ids` | Security group IDs for agents in a VPC. |
| `security-group` | A security group name, used when `security-group-ids` is empty. |
| `zone` | An Availability Zone to place agents in. |
| `Tenancy` | EC2 tenancy (for example `dedicated`). Defaults to `default`. Note the capital T. |
| `vpc` | `true` if Elastic IPs for these agents are VPC addresses. |
| `public-ip` | An Elastic IP to bind when a single agent is launched. |

!!! note
    The element is `instance-description`. Older copies of the settings file, including `assets/settings-all-in-one.xml`, spell it `instance-descripion`; Tank ignores that spelling.

### Instance types

The agent sizes users can choose, and the figures Tank uses to estimate cost and agent count:

```xml
<instance-types type="amazon">
  <type name="m.xlarge" types="m8g.xlarge,m7g.xlarge" cost=".1795" users="2000"
        cpus="4" ecus="16" mem="16" default="true"
        jvmArgs="-XX:InitialRAMPercentage=70.0 -XX:MaxRAMPercentage=70.0 -XX:+UseG1GC" />
</instance-types>
```

| Attribute | Description |
|-----------|-------------|
| `name` | The name shown to users. |
| `types` | Required. A comma-separated list of EC2 instance types for this size. |
| `cost` | Hourly cost, for estimates. |
| `users` | Users one agent of this size runs. |
| `cpus`, `ecus`, `mem` | Informational sizes. |
| `jvmArgs` | JVM options for the agent's test harness. |
| `default` | `true` for the size selected by default. |

The default file's sizes are Graviton (arm64) types, so the agent AMI must be arm64.

### Watchdog

The watchdog checks that launched agents report in, and relaunches them if they don't. Durations are written like `30s`, `3m` or `1h`.

| Element | Default | Description |
|---------|---------|-------------|
| `watchdog/max-time-for-agent-report` | `3m` | How long to wait for agents to report before relaunching them. |
| `watchdog/max-time-for-agent-report-<region>` | the value above | The same for one region, named by its AWS code, for example `max-time-for-agent-report-us-east-2`. |
| `watchdog/max-restarts` | `3` | Relaunch attempts before the job is stopped. |
| `watchdog/sleep-time-between-check` | `30s` | How often the watchdog checks. |

### Results store

Where `DatabaseResultsReporter` writes results:

```xml
<results>
  <provider>com.intuit.tank.persistence.databases.CloudWatchDataSource</provider>
  <config></config>
</results>
```

If `provider` is missing, Tank uses `AmazonDynamoDatabaseDocApi`; the default file sets `CloudWatchDataSource`.

| Provider class (`com.intuit.tank.persistence.databases.`) | Keys in `results/config` |
|-------------------------------------|---------------------------|
| `CloudWatchDataSource` | None. Writes metrics to the `Intuit/Tank` namespace. |
| `AmazonDynamoDatabaseDocApi` | `read-capacity` (default 10), `write-capacity` (default 50). |
| `S3Datasource` | `bucket` (default `tank-test`), `metricString` (default `test.tank.transaction`), `tags`. |
| `WavefrontDatasource` | `wavefrontHost`, `wavefrontPort`, `metricString`. |
| `GraphiteDatasource` | `graphiteHost`, `graphitePort`. |

### Agent metrics

The controller reads agent CPU, memory, JVM and TCP metrics from CloudWatch for the Agent Tracker. Agents must publish them (see the CloudWatch agent setup in `aws-config/tank_agent.yml`).

| Element | Default | Description |
|---------|---------|-------------|
| `agent-metrics/enabled` | `true` | Turns collection on or off. |
| `agent-metrics/poll-interval-seconds` | `60` | How often to poll CloudWatch. Minimum 15. |
| `agent-metrics/retention-points` | `60` | Data points kept per agent. Minimum 2. |
| `agent-metrics/finished-job-grace-minutes` | `15` | How long to keep collecting after a job finishes. |
| `agent-metrics/vcpus` | `0` | The agents' vCPU count. CloudWatch reports CPU as a percentage of one core; with this set, Tank divides by it. `0` caps the figure at 100 instead. |

## Security: `security`

Defines groups, which groups can do what, and users created on startup. See [Roles and permissions](../roles.md) for what the groups mean.

```xml
<security>
  <groups>
    <group>admin</group>
    <group isDefault="true">user</group>
    <group>script-manager</group>
    <group>project-manager</group>
    <group>job-manager</group>
    <group isDefault="true">guest</group>
  </groups>
  <restrictions>
    <restriction operation="CREATE_PROJECT">
      <groups>
        <group>user</group>
        <group>project-manager</group>
      </groups>
    </restriction>
    <!-- ... -->
  </restrictions>
  <users>
    <user>
      <name>admin</name>
      <password>admin</password>
      <email>admin@example.com</email>
      <group>admin</group>
    </user>
  </users>
</security>
```

| Element | Description |
|---------|-------------|
| `groups/group` | The groups that exist. Tank creates any that are missing on startup. `isDefault="true"` groups are given to new users. |
| `restrictions/restriction` | For each `operation`, the groups allowed to perform it. The default file covers `CREATE_PROJECT`, `DELETE_PROJECT`, `EDIT_PROJECT`, `CREATE_SCRIPT`, `DELETE_SCRIPT`, `EDIT_SCRIPT`, `CREATE_FILTER`, `DELETE_FILTER`, `EDIT_FILTER`, `CREATE_DATAFILE`, `DELETE_DATAFILE` and `CONTROL_JOB`. Admins can do everything. |
| `users/user` | Users created on startup if no user with that `name` exists, with `password`, `email` and one or more `group` entries (plus the default groups). Existing users are never changed, so editing the password here after the first start has no effect. |

## Single sign-on: `oidc-sso`

Add this section to let users sign in through an OpenID Connect provider. If it's absent, SSO is off.

```xml
<oidc-sso>
  <client-id>...</client-id>
  <client-secret>/path/to/ssm/parameter</client-secret>
  <authorization-url>https://idp.example.com/oauth2/authorize</authorization-url>
  <redirect-url>https://tank.example.com/v2/auth/sso/callback</redirect-url>
  <issuer>idp.example.com</issuer>
  <token-endpoint>/oauth2/token</token-endpoint>
</oidc-sso>
```

| Element | Description |
|---------|-------------|
| `client-id` | The client ID registered with the provider. The ID token's audience must match it. |
| `client-secret` | The client secret, or, if it starts with `/`, the name of an SSM SecureString parameter that holds it. |
| `authorization-url` | The provider's authorization URL users are sent to. |
| `redirect-url` | The callback URL registered with the provider. The REST API's callback, `<controller-url>/v2/auth/sso/callback`, completes the login and returns the user to the page that started it. |
| `issuer` | The provider's host name. Tank calls `https://<issuer><token-endpoint>` to exchange the code. |
| `token-endpoint` | The path of the token endpoint on that host. |

`OidcSsoConfig` also defines `authorization-endpoint`, but nothing reads it.

## Script functions: `logic-step`

`logic-step/insert-before` and `logic-step/append-after` hold JavaScript that Tank adds before and after every logic step script. The default file defines helper functions such as `log`, `getVariable`, `setVariable`, `getResponseBody`, `gotoGroup` and `abortScript`. Leave them unless you're adding helpers of your own.

## Settings with no effect

The default file and older guides contain these elements, but current code doesn't read them. You can delete them.

| Element | Note |
|---------|------|
| `agent-config/agent-http-port` | The port setting is `agent-port`. |
| `agent-config/ssl-timeout` | Not read. |
| `agent-config/log-post-request`, `agent-config/log-variables` | Not read. |
| `timing-file-storage` | Not read. |
| `vm-manager/watchdog/max-time-for-agent-start` | Not read; only `max-time-for-agent-report` is used. |
| `vm-manager/default-instance-description/reuse-instances` | Not read. |
| `vm-manager/results/write-capacity` | Not read. DynamoDB capacity is set in `results/config`. |
| `mail` | Not read. No current code sends email. |
