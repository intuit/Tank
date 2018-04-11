#!/bin/sh
VM_ARGS="-Dnetworkaddress.cache.ttl=0 -Djsse.enableSNIExtension=false";
#tar -zxvf /opt/tank_agent/monitor.tgz -C /opt/tank_agent >>startup.txt
cd /opt/tank_agent
SERVER_NAME=`hostname`
#MONITOR_ARGS="-javaagent:/opt/tank_agent/monitor/appServer/javaagent.jar -Dserver.name=$SERVER_NAME -Dappdynamics.agent.applicationName=Turboscale-QA"
MONITOR_ARGS=

# create symlink
mkdir /opt/logs>>startup.txt
rm /opt/logs/prod>>startup.txt
rm /opt/logs/dev>>startup.txt
ln -s /opt/tank_agent/logs /opt/logs/dev>>startup.txt
STARTUP_CMD=$1
shift;
echo executing command java -cp /opt/tank_agent/apiharness-1.0-all.jar $@ $VM_ARGS $MONITOR_ARGS com.intuit.tank.harness.APITestHarness $STARTUP_CMD>>startup.txt
nohup java -cp /opt/tank_agent/apiharness-1.0-all.jar $@ $VM_ARGS $MONITOR_ARGS com.intuit.tank.harness.APITestHarness $STARTUP_CMD&
#echo 'starting appdynamics system agent'>>startup.txt
#nohup java -jar /opt/tank_agent/monitor/machine/machineagent.jar&
echo DONE>>startup.txt
