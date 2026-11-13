#!/bin/bash
# The controller's base url, including the context path if it isn't deployed as ROOT.war
controller="[controllerUrl]"
# The agent-token from the controller's settings.xml
token="[agentToken]"
host=`hostname`
capacity=4000
cd "$(dirname "$0")"
nohup java -jar agent-standalone-all.jar -controller=$controller -token=$token -host=$host -capacity=$capacity &
