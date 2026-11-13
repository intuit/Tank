![](https://raw.githubusercontent.com/intuit/tank/master/assets/TankLogo.gif)

[![Build Status](https://api.travis-ci.com/intuit/Tank.svg?branch=master)](https://app.travis-ci.com/intuit/Tank)
![Build Status](https://codebuild.us-east-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiT1Nuc1prblk5Y0E3a05mRjF0QUtBblI0Rk1zTjZyZi9NMWNCaFg4cGlzL1dFN0xVMHgzcGt1ZCtBdFZjNWpMRkhXbGN2T1ZKSmpZbGQ3YjhyaWkzdkJBPSIsIml2UGFyYW1ldGVyU3BlYyI6IlpGbU5vTHM2cHQrbUowOVkiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)
[![GitHub release](https://img.shields.io/github/release/intuit/tank.svg)](https://github.com/intuit/Tank/releases)
[![java](https://img.shields.io/badge/java-21%20%7C%2025-blue.svg)](https://aws.amazon.com/corretto/)
[![tomcat](https://img.shields.io/badge/tomcat-10.1%20%7C%2011.0-blue.svg)](http://tomcat.apache.org/)
# Intuit Tank

Intuit Tank is a load test platform that runs in a cloud environment. It currently supports Amazon web interfaces and utilizes services from EC2, S3, CloudWatch (Logs/Metrics).

Intuit Tank has two main components: A controller and agents.

The Controller is the central hub in the deployment. It supports the GUI for managing tests and for orchestrating. It utilizes Apache Tomcat for a web container and communicates with the agents via http. 
It exposes a RESTful interface for invoking services. Data is stored in a MySql database, on the file system, and reporting metrics can be stored in CloudWatch Metrics, S3, DynamoDB or Wavefront.

Agents are instantiated on demand and exist for the duration of a test. They communicate with the controller via RESTful interfaces.

## Documentation
All of Tank's documentation is at [intuit.github.io/Tank](https://intuit.github.io/Tank/): installation, configuration, the user guide, script functions, the tools, and the REST API reference.

The site is built with [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) from the `docs` folder and published by the `Docs` GitHub Actions workflow on every merge to master. To preview your changes locally:

```bash
python3 -m venv venv && source venv/bin/activate
pip install -r docs-requirements.txt
mkdocs serve        # http://127.0.0.1:8000, reloads as you edit
mkdocs build --strict   # what CI runs; fails on broken links
```

The API reference page reads `docs/openapi-spec.json`, which is generated from the code rather than committed. To include it locally:

```bash
mvn -pl rest-mvc/impl -am test -Dtest=OpenApiSpecExportTest -Dsurefire.failIfNoSpecifiedTests=false
cp rest-mvc/impl/target/openapi/tank-openapi.json docs/openapi-spec.json
```

## Building Intuit Tank
Intuit Tank uses Maven and should be able to be built using public repositories. 
You may need to increase the default memory settings for maven to build. e.g. export MAVEN_OPTS="-Xmx1g"

There are several profiles:
* default -- builds source but does not package tools.
* release -- default plus all tools and signs them using a self signed certificate.
* coverage -- runs jacoco code coverage.
* static-analysis -- runs checkstyle and findbugs.

`mvn clean install` runs all unit tests and integration tests by default under their respective maven stages - `test` and `verify`.

There are several artifacts that are important.
* web/web_ui/target/tank.war -- the main deployment file. Intended for deployment to a tomcat web server.
* agent/agentManager_pkg/target/agent-startup-pkg.zip -- the agent app that gets started when the agent starts and manages communicating with the tank controller and coordinating the tests.
* tools/agent_debugger_pkg/target/Tank-Debugger-all.jar -- the visual debugger jar. can be launched to debug scripts or projects. With --headless it replays a project from the command line and writes the authid and step results to output.log. See tools/agent_debugger/README.md.
* tools/script_filter_pkg/target/Tank-Script-Runner-all.jar -- the visual script filter tool for writing scripts to filter or manipulate scripts on import.
* proxy-parent/proxy_pkg/target/Tank-Proxy-pkg.jar -- the proxy recording tool. A Tool to record scripts using your browser.

## Quickstart
`all-in-one.sh` installs a local controller (Tomcat with an H2 database) and a standalone agent on Mac or Linux,
using the artifacts you build here. It's for trying Tank out and small tests, not for large-scale or production testing.

```bash
mvn clean install -P release -DskipTests
./all-in-one.sh ~/tank-aio
~/tank-aio/start.sh    # then open http://localhost:8080 (admin / admin)
```

See the [Quickstart](https://intuit.github.io/Tank/installation-guide/quickstart/) for details.

## Integration Tests

The integration tests are located in the `test_support` module. They are designed to test the REST API endpoints against the QA Tank environment.
You can find more information in the [README](test_support/README.md) file in that module.

## Issues & Contributions
Please [open an issue here on GitHub](https://github.com/intuit/tank/issues/new) if you have a problem, suggestion, or other comment.

Pull requests are welcome and encouraged! There are eclipse code format templates in the dev_environment folder. 
Any contributions should include new or updated unit tests as necessary to maintain thorough test coverage,
and an update to the `docs` folder for any change users will notice.

## License
Tank is provided under the [Eclipse Public License - Version 1.0](http://www.eclipse.org/legal/epl-v10.html)

## FAQ
Please see the [FAQ](https://intuit.github.io/Tank/faq/).
