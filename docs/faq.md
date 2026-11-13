# FAQ

## Do I need to run Tank in AWS?

No, but Tank is designed for it. Without AWS, run the controller in [standalone mode](installation-guide/installation.md#standalone-deployment): set `<standalone>true</standalone>` and start standalone agents on machines you manage, which register with the controller. Keep data files on local disk, and use the REST results reporter or another results store that doesn't need AWS. The [Quickstart](installation-guide/quickstart.md) sets up a local controller this way.

## Can I use my own MySQL server instead of RDS?

Yes. Tank works with any MySQL 8 server it can reach through the `jdbc/watsDS` data source; the CloudFormation template uses RDS MySQL 8.4. If you don't use RDS, arrange your own backups so you don't lose your projects and scripts if the server fails.

## Maven runs out of memory when I build Tank

Give Maven more memory before you build:

```bash
export MAVEN_OPTS="$MAVEN_OPTS -Xms2g -Xmx3g"
```

Appending keeps any options you already set in `MAVEN_OPTS`, such as a trust store.

## How do I sign in for the first time?

When the controller starts, it creates each user listed under `security/users` in [`settings.xml`](installation-guide/configuration.md#security-security) that doesn't exist yet. The default file creates `admin` with the password `admin`, in the `admin` group. Sign in with those and change the password straight away.

To use a different first password, change the entry before the controller's first start. Once the user exists, Tank doesn't update it from `settings.xml`, so later edits there have no effect: change the password in the UI instead.

If [single sign-on](installation-guide/configuration.md#single-sign-on-oidc-sso) is configured, users can also sign in through your identity provider.

## Where is the settings file?

Tank reads `settings.xml` from the controller's working directory, or from the folder named by the `WATS_PROPERTIES` environment variable or system property. If there isn't one, Tank writes the default file there on first start. See [Configuration](installation-guide/configuration.md#where-the-file-lives).

## Where did the old UI go?

Since Tank 5.0.0 the React UI is at the root of the controller, and the original JSF UI is at `/classic/`, for example `http://localhost:8080/classic/`.

## How do I call the REST API from a script?

Create an API token for your user and send it as `Authorization: Bearer <token>`. See [Using the REST API](api/index.md).

## My agents start but the test never runs

The controller's watchdog relaunches agents that don't report in time (`max-time-for-agent-report`, 3 minutes by default) and stops the job after `max-restarts` attempts. Check that:

- Agents can reach the controller at `controller-base-url`.
- The controller can reach agents on the agent port, TCP 8090 by default (`agent-port`).
- The agent AMI's architecture matches the instance types. The default instance types are Graviton (arm64).
- When `rest-security-enabled` is `true`, `agent-token` is set.
- The agent logs, in `/opt/tank_agent/logs/agent.log` on the agent (and in CloudWatch Logs if you installed the CloudWatch agent), show what failed.

## Does Tank need ActiveMQ or another message queue?

No. Older guides describe a JMS queue between the controller and agents. Current Tank uses HTTP and WebSocket only.
