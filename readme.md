![](https://raw.githubusercontent.com/intuit/tank/master/assets/TankLogo.gif)

[![Build Status](https://api.travis-ci.com/intuit/Tank.svg?branch=master)](https://app.travis-ci.com/intuit/Tank)
![Build Status](https://codebuild.us-east-2.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiT1Nuc1prblk5Y0E3a05mRjF0QUtBblI0Rk1zTjZyZi9NMWNCaFg4cGlzL1dFN0xVMHgzcGt1ZCtBdFZjNWpMRkhXbGN2T1ZKSmpZbGQ3YjhyaWkzdkJBPSIsIml2UGFyYW1ldGVyU3BlYyI6IlpGbU5vTHM2cHQrbUowOVkiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)
[![GitHub release](https://img.shields.io/github/release/intuit/tank.svg)](https://github.com/intuit/Tank/releases)
[![java](https://img.shields.io/badge/java-11-blue.svg)](https://aws.amazon.com/corretto/)
[![tomcat](https://img.shields.io/badge/tomcat-8%20%7C%208.5%20%7C%209-blue.svg)](http://tomcat.apache.org/)
# Intuit Tank

Intuit Tank is a load test platform that runs in a cloud environment. It currently supports Amazon web interfaces and utilizes services from EC2, S3, CloudWatch (Logs/Metrics).

Intuit Tank has two main components: A controller and agents.

The Controller is the central hub in the deployment. It supports the GUI for managing tests and for orchestrating. It utilizes Apache Tomcat for a web container and communicates with the agents via http. 
It exposes a RESTful interface for invoking services. Data is stored in a MySql database, on the file system, and reporting metrics can be stored in CloudWatch Metrics, S3, DynamoDB or Wavefront.

Agents are instantiated on demand and exist for the duration of a test. They communicate with the controller via RESTful interfaces.

## Building Intuit Tank
Intuit Tank uses Maven and should be able to be built using public repositories. 
You may need to increase the default memory settings for maven to build. e.g. export MAVEN_OPTS="-Xmx1g"

There are several profiles (for the initial build you should build the release profile so that the installation guide is built. e.g. mvn clean install -P release)
* default -- builds source but does not build the docs or package tools.
* release -- default plus docs and all tools and signs them using a self signed certificate.
* coverage -- runs jacoco code coverage.
* static-analysis -- runs checkstyle and findbugs.


There are several artifacts that are important.
* web/web_ui/target/tank.war -- the main deployment file. Intended for deployment to a tomcat web server.
* agent/agentManager_pkg/target/agent-startup-pkg.zip -- the agent app that gets started when the agent starts and manages communicating with the tank controller and coordinating the tests.
* tools/agent_debugger_pkg/target/Tank-Debugger-all.jar -- the visual debugger jar. can be launched to debug scripts or projects.
* tools/script_filter_pkg/target/Tank-Script-Runner-all.jar -- the visual script filter tool for writing scripts to filter or manipulate scripts on import.
* proxy-parent/proxy_pkg/target/Tank-Proxy-pkg.jar -- the proxy recording tool. A Tool to record scripts using your browser. 

## Quickstart
There is a shell script to install and configure a standalone controller and agent and configured with a java database for 
Mac and Linux. Windows users should install some POSIX tooling such as [Babun](http://babun.github.io) or [Cygwin](https://www.cygwin.com). 
It can be downloaded from our [public site](http://tank-public.s3-website-us-east-1.amazonaws.com/all-in-one.sh) or 
in the root of the distribution. You can use this version for small tests to try out the tools but should not use 
it for large scale or production testing.

## Guides
Installation guide and User guide can be found in the docs folder and are built with the source. and can also be found on our [wiki](https://github.com/intuit/Tank/wiki).

## Issues & Contributions
Please [open an issue here on GitHub](https://github.com/intuit/tank/issues/new) if you have a problem, suggestion, or other comment.

Pull requests are welcome and encouraged! There are eclipse code format templates in the dev_environment folder. 
Any contributions should include new or updated unit tests as necessary to maintain thorough test coverage.

## License
Tank is provided under the [Eclipse Public License - Version 1.0](http://www.eclipse.org/legal/epl-v10.html)

## FAQ
Please see our [FAQ on our wiki](https://github.com/intuit/Tank/wiki/FAQ).
