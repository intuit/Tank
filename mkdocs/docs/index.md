# Welcome to Intuit Tank

Intuit Tank(called `The Tank` for brevity) is a open-source load test platform from Intuit that runs in a cloud environment. It currently supports Amazon web interfaces and utilizes services from `EC2`, `S3` and `DynamoDB`.

## Constituents
Tank has two main components: A Controller Interface and (one or many) agent(s).

### Controller
The Controller is the central hub/Control Plane for Tank.It supports the GUI for managing tests and for orchestrating.   
It utilizes Apache Tomcat for a web container and ActiveMQ as a message queue for communication.   
It exposes a `REST`ful interface for invoking services. It stores data in a `SQL` database, and uses a `AWS S3` bucket as a shared filesystem.   
The Controller also interfaces with an instance of a `JMS` Queue (`ActiveMQ`), usually on the same instance as the controller. The Message Queue is used to coordinate starting and coordination of the load tests.   
The Controller can be accessed at the context root of `http://[baseUrlOfAmazonInstance]/tank`

### Agent(s)
Agent(s) are instantiated on demand and exist for the duration of a test. They communicate with the controller via a combination of RESTful interfaces as well as the Message Queue.

### Optional Components
Optional components can include a *log aggregator* as well as other instances to be started at the beginning of a test. These are configured in the `settings.xml` and can be stopped and re-used or terminated at the end of a test.

The next few sections will delve deep into the Key Concepts of Tank and Tools that assist in making Load testing with Tank a seamless experience.