# Set-up & Installation

## Preface

The purpose of this guide is to describe the IT concepts and strategies employed by Tank.

Tank is meant to be an integrated testing solution for driving load against production systems. It includes a central controller for coordinating test agents and driving the UI for setting up and reporting on the tests.   
The agents are instantiated on demand and terminated when they are no longer needed. This helps keep the costs of running large tests to a minimum.  

Tank is meant to be a load test solution and is not currently optimized for functional testing. This does not mean that it is not useful for functional testing, but many of the paradigms and toolsets needed for functional testing may be lacking.  

### Cloud Services Used
Tank uses a gamut of Cloud Service to fulfill the mission of Load Testing. The below section (expand for more info) gives a brief on each of the cloud services.

??? abstract "Cloud Services used"

    === "EC2"
        
        Elastic Compute Cloud (`EC2`) is the service that is used to create compute resources to drive load (agents). There are several parts of this service used.
        
        * `Instances` are the actual compute resources used to run load.
        * `Security Groups` are used to control access to and from the agent instances and the controller instance.
        * `AMIs` are used as the templates for instantiating new resources.
        * `Load balancers` are used to distribute load to the controller.
        * `Elastic IPs` are used to bind specific IP addresses to agent instances and can be used in firewall rules to allow access from agents to the datacenter.
        * `Volumes` are used for persisted data that is not stored in `S3`.

    === "S3"
        Simple Storage Service is used to store document-based items such as data files and response time reports.

    === "DynamoDB"
        DynamoDb is a key value data store used to collect response metrics during the execution of a test. They are typically aggregated after the test is run.

    === "RDS"
        Relational Data Service (`MySQL`) is used to store the metadata about the projects including scripts, filters, and project configuration used by the controller.

    === "IAM"    
        Identity and Access Management is used to control access and permissions to cloud resources.



## Environment

??? tip "Payment set-up for creating Cloud Account"
    You will need a Credit Card or arrange payment in order to complete the following steps.  
        
        * Go to http://aws.amazon.com/ and click on the "Sign Up" button.
        * Complete all steps for creating an account.

### Retrieve Security Credentials
Thera are two methods of setting up access to resources from the controller and agent instances.  

!!! abstract "Access Mechanism"
    
    === "IAM role"

        Use IAM roles to start the instances giving them access to the needed resources. If you use this method, just make sure you start your resources with the appropriate roles.

    === "Access Keys"
        
        * Log in to the amazon console from `http://aws.amazon.com/`
        * In the top right corner where the account name shows up, click it and select `Security Credentials` from the drop down menu.
        * Scroll down to the `Access Keys` section and click `Create a new Access Key`.
        * Copy the `Access Key ID` from the newly created key.
        * Click on `Show` section and copy the `Secret Access Key`.
        * Store these values in a file on your computer somewhere, you will need them for configuration later.


### Creating Essential Resources


!!! abstract "Creating Essential Resources"
    
    === "Key Pair"

        The keypair is used to ssh into the instances that get started. Do not lose these keys as they cannot be retrieved form AWS.

        * Log in to the amazon console from `http://aws.amazon.com/`
        * Go to `EC2` tab
        * Click on `Key Pairs` link under the `Network & Security` section in left navigation panel
        * Create a Key Pair and save the downloaded `pem` file somewhere safe `ssh-keygen -y -f [path/to/pem/file].pem [path/to/key/file].pub`
        * Change the Mod settings on the key file to 600 `sudo chmod 600 [path/to/pem/file].pem [path/to/key/file].pub`

        !!! tip "Tip"
            Optionally create an `environment variable` for the key. We will use the environment variable `AWS_KEY` in the rest of this documentation to refer to the key file. You could add the following command into your `bashrc` or `profile` file as well. `export AWS_KEY=/Home/aws_keys/tank_key.pem`

    === "Security Group"

        * Log in to the amazon console `from http://aws.amazon.com/`
        * Go to `EC2` tab
        * Click on `Security Groups` link under the `Network & Security` section in left navigation panel
        * Click the `Create Security Group` button in the tool bar area
        * Name the security group something appropriate like tank.
        * Select the new Group and click the `Inbound tab` in the lower panel.
        * Add appropriate rules. At a minimum, you will need `SSH`, `HTTP` (80 and 8080), `HTTPS`(443 and 8443), and `8090`(agent)


    === "RDS Instance"

        If you wish, you can create your own `MySQ`L instance and point to it instead.

        * Log in to the amazon console from `http://aws.amazon.com/`
        * Go to `RDS` tab
        * Click on `Instaces` link in left navigation panel
        * Click the `Launch DB Instance` button in the tool panel
        * Select `mysql` instance
        * Set all options appropriately.



    === "Base AMI"  
        Agent AMIs must be created in each of the Amazon regions that you wish to run tests from. You can use `Cloudformation` or   `chef` to provision your instances if you wish.
        
        !!! tip "Tip"
            There is a Cloudformation script and resources in aws-config in the root of this project's directory that will create the controller and the agent instances that can be used as a starting point for creating the AMIs.


        * Log in to the amazon console from http://aws.amazon.com/
        * Go to EC2 tab
        * Click on AMIs link under the Images section in left navigation panel
        * Find a suitable 64-bit base image backed by EBS and launch an instance. It does not matter which size or zone you start it in as it will only be used to create a base AMI.
        * After it is started, connect via ssh
        * Download and install the latest version of Java and add an entry in /etc/profile for a JAVA_HOME variable pointing to the base java directory.
        * Ensure that java is installed and that the JAVA_HOME environment variable is set in /etc/profile
        * Set the maximum number of open files limit to at least 50000 `echo ulimit -n 50000 >>/etc/profile`
        * Select the instance and choose Create Image (EBS AMI) from the Instance Actions dropdown in the toolbar area.
        * Give it an appropriate name such as Intuit Tank Base and click OK.
        * After the instance is ready, terminate the instance you started.

    === "Controller Instance and its AMI"
        * Log in to the amazon console from http://aws.amazon.com/
        * Go to EC2 tab
        * Click on AMIs link under the Images section in left navigation panel
        * Launch an instance from your Intuit Tank Base AMI. Select m1.xlarge and the zone your EBS volume is installed in.
        * Click on Elastic IPs link under the Network and Security section in left navigation panel
        * Click the Allocate New Address button.
        * Click the new address and click the Associate Address button.
        * Select the new instance you just launched and select ok.
        * Connect to the instance `ssh -i $AWS_KEY root@[instance]`
        * Download and Install Tomcat 6x from apache `wget http://tomcat.apache.org/download-60.cgi`
        * Download mysql connector and install in TOMCAT_HOME/lib `wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.30.tar.gz` or `or wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.30.zip`
        * Uncompress and move the jar file mysql-connector-java-5.1.30-bin.jar to TOMCAT_HOME/lib
        * Download Weld support jar and install in TOMCAT_HOME/lib `wget -O TOMCAT_HOME/lib http://central.maven.org/maven2/org/jboss/weld/servlet/weld-tomcat-support/1.0.1-Final/weld-tomcat-support-1.0.1-Final.jar`
        * Upload Intuit Tank war file from your build machine `upload PROJECT_ROOT/web/web_ui/target/tank.war from your local machine.`
        * Move it to the webapps dir `For context of /tankmv tank.war TOMCAT_HOME/webapps/` and `For context of /mv tank.war TOMCAT_HOME/webapps/ROOT.war`
        * Create the tank directories. `mkdir /mnt/ebs/wats; mkdir /mnt/ebs/wats/conf; mkdir /mnt/ebs/wats/jars`
        * Add Datasource definition to server.xml ``

    === "Agent AMI"
        
        * Log in to the amazon console from http://aws.amazon.com/
        * Go to EC2 tab
        * Click on AMIs link under the Images section in left navigation panel
        * Launch an instance from your Intuit Tank Base AMI.
        * Connect to the instance ssh -i AWS_KEY root@[instance]
        * Upload the agent startup zip file from your build machine PROJECT_ROOT/agent/agent_startup_pkg/target/agent-startup-pkg.zip
        * unzip the file to /opt cd /opt ; unzip ~/agent-startup-pkg.zip
        * Move startup script to /etc/init.d mv tank_agent/tank-agent /etc/init.d/
        * Set it to start on startup chkconfig tank-agent on
        * Select the instance and choose Create Image (EBS AMI) from the Instance Actions dropdown in the toolbar area.
        * Give it an appropriate name such as Intuit Tank Agent and click OK.
        * After the instance is ready, terminate the instance you started.

## Standalone Deployment
For standalone deployments, you will need to install the controller, database, and as many agent machines as needed. These steps assume that you have java installed on all machines needed.  

??? abstract "Standalone deployment"

    === "Install Server Components"
        On the machine that wil server as the controller.

        * Download and Install `Tomcat 6x` from apache `http://tomcat.apache.org/download-60.cgi`
        * Download mysql connector and install in `TOMCAT_HOME/lib` 
            `wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.30.tar.gz`
        * Uncompress and move the jar file `mysql-connector-java-nnnn.jar` to `TOMCAT_HOME/lib`
        * Download Weld support jar and install in TOMCAT_HOME/lib `wget -O TOMCAT_HOME/lib http://central.maven.org/maven2/org/jboss/weld/servlet/weld-tomcat-support/1.0.1-Final/weld-tomcat-support-1.0.1-Final.jar`
        * Build the project `mvn clean install -P release`
        * Move the war to the tomcat webapp directory as tank.war or ROOT.war `For context /tank mv PROJECT_ROOT/web/web_ui/target/tank.war /opt/tomcat/webapps/tank.war` AND `For context /mv PROJECT_ROOT/web/web_ui/target/tank.war /opt/tomcat/webapps/ROOT.war`
        * Install `MySQL` server
        * Change the root password `mysqladmin -u root password 'NEW_PASSWORD'`
        * Start `MySQL` client `mysql –u root –p`
        * Create tank schema `CREATE SCHEMA wats DEFAULT CHARACTER SET utf8;`
        * Create the tank directories. `mkdir [TS_HOME]; mkdir [TS_HOME]/conf; mkdir [TS_HOME]/jars`
        * Add variable declarations to `/etc/profile` and add the following `export WATS_PROPERTIES=[TS_HOME]/conf`
        * Start and stop tomcat to initialize the system. Ensure that the settings file is created in $WATS_PROPERTIES and that the database tables are created.
        * Edit the `settings.xml` file. Change the standalone entry to true and change any other settings.
        * Add Datasource definition to `server.xml`. Edit the file `[TOMCAT_HOME]/conf/server.xml`. Inside the <GlobalNamingResources> tag add the following replacing values appropriately.  
            ```<Resource name="jdbc/watsDS" auth="Container"
                    type="javax.sql.DataSource"
                    maxActive="100" maxIdle="30" maxWait="10000"
                    username="admin"  password="replace_password_here"
                    driverClassName="com.mysql.jdbc.Driver"
                    url="jdbc:mysql://replace_host_name_here:3306/replace_db_name_here?autoReconnect=true" />
            ```
        * Add Datasource definition to context.xml. Edit the file [TOMCAT_HOME]/conf/context.xml. Inside the <Context> tag add the following:  
            ```<ResourceLink global='jdbc/watsDS' name='jdbc/watsDS'
                    type="javax.sql.Datasource" />
            ```

    === "Create Agents"
        Agents will typically run on separate machines. The default is `4000` users per agent but that number can be adjusted by editing the `run.sh` script.

        * Upload the agent standalone zip file `PROJECT_ROOT/agent/agent_standalone_pkg/target/agent-standalone-pkg.zip`
        * unzip the file `unzip agent-standalone-pkg.zip`
        * Read the `README.txt` for further instructions.

## Deployment Strategy    
Build the project from the root using the release profile.

`mvn clean install -P release`

Deployment is currently done uploading the war to the TOMCAT_HOME/webapps directory and restarting the server.

Deployment can also be done via Cloudformation scripts that create new controller resources and then switching the load balancer to point at the new resource. This is the preferred method as it requires no downtime.

## Configuration
Configuration is achieved via an XML file called `settings.xml`. The default is `[TOMCAT_HOME]/settings.xml`. The directory location can be specified by setting the environment variable `WATS_PROPERTIES`.

!!! tip "Tip"

    * To get the default settings file, start the server and then stop it. If the `settings.xml` file is not available, the default settings will be created. You can then edit it.
    * This configuration file is broken into several different sections. Each section is independent of the other sections and can occur in any order within the document.
    * Relative paths are from the `tomcat` home.


### Configuration Entries

??? abstract "Configuration Entries"

    === "Global Configuration Entries"

        ``` xml
            <!-- The server side storage directory for datafiles. 
                Can be an absolute or relative path or an s3 bucket by prefixing a bucket with s3: e.g. s3:tank-data-files -->
                <data-file-storage>datafiles</data-file-storage>
                
                <!-- The server side storage directory for timing data.
                Can be an absolute or relative path or an s3 bucket by prefixing a bucket with s3: e.g. s3:tank-timing-files -->
                <timing-file-storage>timing</timing-file-storage>
            
                <!-- The server side storage directory for agent files to be sent to agents. All files in this directory will be sent to each agent.
                to override the default agent startup behavior, include a start_wats.sh script with modifications.
                Can be an absolute or relative path or an s3 bucket by prefixing a bucket with s3: e.g. s3:tank-agent-supports -->
                <jar-file-storage>jars</jar-file-storage>
            
                <!-- The server side storage directory for temp script files. must be local file system. -->
                <tmp-file-storage>tmpfiles</tmp-file-storage>
                
                <!-- name of this instance (no spaces). used in timing table names. -->
                <instance-name>tank</instance-name>
            
                <!-- The baseUrl for the controller. Must include the context e.g. tank) -->
                <controller-base-url>http://localhost:8080/tank</controller-base-url>
                
                <!-- if this instance is a standalone instance and should not start agents automatically. -->
                <standalone>false</standalone>
                
                <!-- if this rest calls require authentication using api key or username/password or logged in via session -->
                <rest-security-enabled>true</rest-security-enabled>
                
                <!-- Products that can be selected by users for categorization -->
                <products>
                <product name="">All Products</product>
                <product name="MyProduct">My Product</product>
                </products>
            
                <!-- Locations used for informational purposes only. -->
                <locations>
                <location name="unspecified" displayName="Unspecified" />
                <location name="san_diego" displayName="San Diego\, CA" />
                </locations>
                
                <!-- mail configuration for notifications -->
                <mail>
                <mail.smtp.host>localhost</mail.smtp.host>
                <mail.smtp.port>25</mail.smtp.port>
                <from-email>do_not_reply@myCompany.com</from-email>
                </mail>
                
                <reporting>
                <provider>
                <!-- Reporter that uses key value database. currently DynamoDB -->
                <reporter>com.intuit.tank.reporting.db.DatabaseResultsReporter</reporter>
                <reader>com.intuit.tank.reporting.db.DatabaseResultsReader</reader>
                <config></config>
                
                </provider>
                
                <!-- Use this for Rest reporting directly to the controller. 
                Only for standalone or all in one. will over tax the controller if used for large load -->
                <!-- <provider>
                <reporter>com.intuit.tank.reporting.rest.RestResultsReporter</reporter>
                <reader>com.intuit.tank.reporting.rest.RestResultsReader</reader>
                </provider> -->
            </reporting>
        ```

    === "Agent Configuration Entries"

        ``` xml
            <agent-config>
                <!-- Times used when calculating estimated run time -->
                <duration-simulation>
                    <simulation for="post" min="500" max="1000" />
                    <simulation for="get" min="50" max="300" />
                    <simulation for="process" min="10" max="50" />
                </duration-simulation>
            
                <!-- Where to store csv files on the agent -->
                <agent-data-file-storage>/tmp</agent-data-file-storage>
            
                <!-- port that controller talks to agent on -->
                <agent-http-port>8090</agent-http-port>
            
                <!-- Max size of response body to log on error -->
                <max-body-report-size>5000</max-body-report-size>
            
                <!-- time to wait before re-establishing ssl -->
                <ssl-timeout>360000</ssl-timeout>
            
                <!-- connection timeout setting. set to zero to not have a timeout -->
                <connection-timeout>15000</connection-timeout>
            
                <status_report_interval_milis>30000</status_report_interval_milis>
            
                <!-- Max response time before waiting additional time between requests. (in ms.) -->
                <max-response-time>5000</max-response-time>
            
                <!-- Max time to wait if if response was too slow. Helps prevent backups if server is running too slow. (in ms.) -->
                <max-failed-wait-time>180000</max-failed-wait-time>
            
                <!-- Max amount of time after simulation time to run the test. -->
                <over-simulation-max-time>7200000</over-simulation-max-time>
            
                <!-- set to true to log each post request. -->
                <log-post-request>false</log-post-request>
            
                <!-- set to true to log each post response. -->
                <log-post-response>false</log-post-response>
            
                <!-- set to true to log each variable set or unset. -->
                <log-variables>false</log-variables>
            
                <!-- Mime type rgex for logging of response body on error. -->
                <valid-mime-types>
                    <mime-type-regex>.*text.*</mime-type-regex>
                    <mime-type-regex>.*json.*</mime-type-regex>
                    <mime-type-regex>.*xml.*</mime-type-regex>
                </valid-mime-types>
                
                <!-- add any headers that should be added to each request -->
                <request-headers>
                    <header key="test_flag">test_flag</header>
                </request-headers>
            
                <!-- configured TankHttpClients. -->
                <tank-http-clients>
                    <tank-client name="Apache HttpClient 3.1">com.intuit.tank.httpclient3.TankHttpClient3</tank-client>
                    <tank-client name="Apache HttpClient 4.5">com.intuit.tank.httpclient4.TankHttpClient4</tank-client>
                    <tank-client name="OkHttp Client 2.5">com.intuit.tank.okhttpclient.TankOkHttpClient</tank-client>
                </tank-http-clients>
                <default-tank-client>Apache HttpClient 4.5</default-tank-client>
            
                </agent-config>            
            
        ```

    === "VM Manager Entries"

        ``` xml
            <vm-manager>
                <default-region>US_EAST</default-region>
                <!-- <default-region>US_WEST_1</default-region> -->
                <!-- set to true to default use of available eips for agents -->
                <use-agent-elastic-ips>false</use-agent-elastic-ips>
                <!-- reserved eips will not be used -->
                <reserved-elastic-ips>
                    <!-- <eip>0.0.0.0</eip> -->
                </reserved-elastic-ips>
            
                <!-- Default instance description items. can be overridden in instance descriptions. -->
                <default-instance-description>
                    <security-group>security_group</security-group>
                    <keypair>myKey</keypair>
                    <!--  if using roles. recommended over using security keys -->
                    <!-- <iam-role>role</iam-role> -->
                    <!--  optional if running ni a subnet in a vpc, use the subnetIds -->
                    <!-- <vpc-subnet>subnet-472b9f30</vpc-subnet>
                    <security-group-ids>sg-1234aaa, sg-1234bbb</security-group-ids> -->
                    <reuse-instances>false</reuse-instances>
                </default-instance-description>
            
                <!-- Credentials for the cloud provider. It is recommended that users us IAM Roles for accessing services insead of embedding AWS keys -->
                <credentials type="amazon">
                <!-- Set credentials via system or java properties. -->
                    <secret-key-id-property>AWS_SECRET_KEY_ID</secret-key-id-property>
                    <secret-key-property>AWS_SECRET_KEY</secret-key-property>
                    <!-- Set if you want to specifiy credentials directly here. less secure! -->
                    <!-- 
                    <secret-key-id>[AWS_SECRET_KEY_ID]</secret-key-id>
                    <secret-key>[AWS_SECRET_KEY]</secret-key>
                    -->
                    <!-- Set if you need a proxy to talk to amazon services -->
                    <!-- 
                    <proxy-host></proxy-host>
                    <proxy-port>80</proxy-port>
                    -->
                </credentials>
            
                <!-- Instance descriptions for regions -->
                <instance-descriptions region="US_WEST_1">
                    <instance-descripion name="AGENT">
                    <ami>[AMI-ID]</ami>
                    <keypair>[KEYPAIR]</keypair>
                    <!--  if using roles. recommended over using security keys -->
                    <!-- <iam-role>role</iam-role> -->
                    <!--  optional if running ni a subnet in a vpc, use the subnetIds -->
                    <!-- <vpc-subnet>subnet-472b9f30</vpc-subnet>
                    <security-group-ids>sg-1234aaa, sg-1234bbb</security-group-ids> -->
                    </instance-descripion>
                </instance-descriptions>
            
                <instance-descriptions region="US_EAST">
                    <instance-descripion name="AGENT">
                    <ami>[AMI-ID]</ami>
                    <keypair>[KEYPAIR]</keypair>
                    <!--  if using roles. recommended over using security keys -->
                    <!-- <iam-role>role</iam-role> -->
                    <!--  optional if running ni a subnet in a vpc, use the subnetIds -->
                    <!-- <vpc-subnet>subnet-472b9f30</vpc-subnet>
                    <security-group-ids>sg-1234aaa, sg-1234bbb</security-group-ids> -->
                    </instance-descripion>
                </instance-descriptions>
                
                <!-- allowed instance types and their costs. update costs as necessary -->
                <instance-types type="amazon">
                    <type name="c3.large" cost=".105" users="500" cpus="2" ecus="7" mem="3.75" jvmArgs="-Xms2560m -Xmx2560m" />
                    <type name="c3.xlarge" cost=".21" users="2000" cpus="4" ecus="14" mem="7.5" jvmArgs="-Xms6g -Xmx6g" />
                    <type name="c3.2xlarge" cost=".42" users="4000" cpus="8" ecus="28" mem="15" default="true" jvmArgs="-Xms12g -Xmx12g" />
                    <type name="c3.4xlarge" cost=".84" users="8000" cpus="16" ecus="55" mem="30" jvmArgs="-Xms125g -Xmx25g" />
                    <type name="c3.8xlarge" cost="1.68" users="16000" cpus="32" ecus="108" mem="60" jvmArgs="-Xms50g -Xmx50g" />
                </instance-types>
            
                <!-- watchdog is used to check if all agents start correctly. -->
                <watchdog>
                    <!-- The maximum amount of time to wait for the agents to start before restarting -->
                    <max-time-for-agent-start>3m</max-time-for-agent-start>
                    <!-- The maximum amount of time to wait for the agents to report to the controller before restarting -->
                    <max-time-for-agent-report>5m</max-time-for-agent-report>
                    <!-- the maximum number of restarts before stopping test -->
                    <max-restarts>2</max-restarts>
                    <!-- The amount of time to wait between checking for agent start -->
                    <sleep-time-between-check>30s</sleep-time-between-check>
                </watchdog>
                
                <!--Configure the results database implementation if reporting is configured via com.intuit.tank.reporting.db.DatabaseResultsReporter -->
                <results>
                <config>
                    <write-capacity>50</write-capacity>
                    <read-capacity>10</read-capacity>
                </config>
                <provider>com.intuit.tank.persistence.databases.AmazonDynamoDatabaseDocApi</provider>
                <!-- <provider>com.intuit.tank.persistence.databases.AmazonSimpleDatabase</provider> -->
                </results>
            
                </vm-manager>        
        
        ```


    === "Security and Access control Entries"    

        ``` xml
            <security>
                <!-- groups that can be assigned to users. set isDefault to tru to have these selected when creating new users -->
                <groups>
                    <group>admin</group>
                    <group isDefault="true">user</group>
                    <group>script-manager</group>
                    <group>project-manager</group>
                    <group>job-manager</group>
                    <group isDefault="true">guest</group>
                </groups>
                <!-- restrictions set which groups can perform which actions. -->
                <restrictions>
                    <!-- Project permissions -->
                    <restriction operation="CREATE_PROJECT">
                    <groups>
                        <group>user</group>
                        <group>project-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="DELETE_PROJECT">
                    <groups>
                        <group>project-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="EDIT_PROJECT">
                    <groups>
                        <group>project-manager</group>
                    </groups>
                    </restriction>
                    <!-- Script permissions -->
                    <restriction operation="CREATE_SCRIPT">
                    <groups>
                        <group>user</group>
                        <group>script-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="DELETE_SCRIPT">
                    <groups>
                        <group>script-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="EDIT_SCRIPT">
                    <groups>
                        <group>script-manager</group>
                    </groups>
                    </restriction>
                    <!-- Filter permissions -->
                    <restriction operation="CREATE_FILTER">
                    <groups>
                        <group>user</group>
                        <group>script-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="DELETE_FILTER">
                    <groups>
                        <group>script-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="EDIT_FILTER">
                    <groups>
                        <group>script-manager</group>
                    </groups>
                    </restriction>
                    <!-- Data File permissions -->
                    <restriction operation="CREATE_DATAFILE">
                    <groups>
                        <group>user</group>
                        <group>script-manager</group>
                        <group>project-manager</group>
                    </groups>
                    </restriction>
                    <restriction operation="DELETE_DATAFILE">
                    <groups>
                        <group>script-manager</group>
                        <group>project-manager</group>
                    </groups>
                    </restriction>
                    <!-- Job permissions -->
                    <restriction operation="CONTROL_JOB">
                    <groups>
                        <group>job-manager</group>
                        <group>project-manager</group>
                    </groups>
                    </restriction>
                </restrictions>
                <!-- Define users to be created on startup if they do not exist. -->
                <users>
                    <user>
                    <name>admin</name>
                    <password>admin</password>
                    <email>email@company.com</email>
                    <group>admin</group>
                    </user>
                </users>
                </security>                    
        ```
