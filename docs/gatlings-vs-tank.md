# Ideology: Gatling vs Tank

[Gatling](https://gatling.io/) and Tank are both open-source load testing tools, but they start from different places.

| | Tank | Gatling (open-source edition) |
| :- | :- | :- |
| **How tests are written** | Recorded through the Tank Proxy, then cleaned up with [filters](key-concepts/filters.md) and edited step by step in a web UI. | Written as code in Gatling's DSL (Java, Scala or Kotlin), often starting from its recorder. |
| **Where tests live** | In the controller's database, shared by everyone who uses the controller. | In source files, versioned with your code. |
| **Where load comes from** | Agents the controller starts on demand in AWS, in one or more regions. | The machine that runs the simulation. |
| **Running and watching tests** | Queued and controlled from the web UI or the [REST API](api/index.md), with live users, TPS and failures per job and per agent in the [Agent Tracker](key-concepts/jobs.md). | Run from the command line or a build tool, with an HTML report at the end. |
| **Test data** | [Data files](key-concepts/datafiles.md) uploaded to the controller and split among agents. | Feeders defined in the simulation code. |
| **Access control** | Users, groups and ownership (see [Roles and permissions](roles.md)). | None; anyone with the code can run it. |

Tank suits teams that want a shared, central place to record, keep and run load tests at scale without writing code. Gatling suits teams that want load tests as code alongside their application.
