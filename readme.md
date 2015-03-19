![](https://github.com/intuit/tank/src/TankLogo.gif)

#Intuit Tank

Intuit Tank is a load test platform that runs in a cloud environment. It currently supports Amazon web interfaces and utilizes services from EC2, S3 and DynamoDb.

Intuit Tank has two main components: A controller and agents.

The Controller is the central hub in the deployment. It supports the GUI for managing tests and for orchestrating. It utilizes Tomcat 6x for a web container communicates with the agents via http. 
It exposes a RESTful interface for invoking services. Data is stored in a MySql database, on the file system, and in amazon DynamoDb.

Agents are instantiated on demand and exist for the duration of a test. They communicate with the controller via a combination of RESTful interfaces as well as the Message Queue.

The Controller also interfaces with an instance of a JMS Queue (ActiveMQ), usually on the same instance as the controller. The Message Queue is used to coordinate starting and coordination of tests.

Optional components can include a log aggregator as well as other instances to be started at the beginning of a test. These are configured in the settings.xml and can be stopped and re-used or terminated at the end of a test.

##Building Intuit Tank
Intuit Tank uses Maven and should be able to be built using public repositories. 

There are several profiles 
* default -- builds source but does not build the docs or package tools.
* release -- default plus docs and all tools and signs them using a self signed certificate.
* clover -- runs clover code coverage.
* static-analysis -- runs checkstyle and findbugs.


There are several artifacts that are important.
* web/web_ui/target/tank.war -- the main deployment file. Intended for deployment to a tomcat web server.
* agent/agentManager_pkg/target/agent-startup-pkg.zip -- the agent app that gets started when the agent starts and manages communicating with the tank controller and coordinating the tests.
* tools/agent_debugger_pkg/target/Tank-Debugger-all.jar -- the visual debugger jar. can be launched to debug scripts or projects.
* tools/script_filter_pkg/target/Tank-Script-Runner-all.jar -- the visual script filter tool for writing scripts to filter or manipulate scripts on import.
* proxy-parent/proxy_pkg/target/Tank-Proxy-pkg.jar -- the proxy recording tool. A Tool to record scripts using your browser. 

##Guides
Installation guide and User guide can be found in the docs folder and are built with the source.

## Issues & Contributions
Please [open an issue here on GitHub](https://github.com/intuit/tank/issues/new) if you have a problem, suggestion, or other comment.

Pull requests are welcome and encouraged! There are eclipse code format templates in the dev_environment folder. 
Any contributions should include new or updated unit tests as necessary to maintain thorough test coverage.

## License
Tank is provided under the [Eclipse Public License - Version 1.0](http://www.eclipse.org/legal/epl-v10.html)