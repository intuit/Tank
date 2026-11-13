#!/bin/sh
# Started by the standalone agent with the harness options for a job (-http, -token, -jobId, -stopBehavior).
# Set TANK_AGENT_JVM_ARGS to size the harness for this machine.
vm_arguments="${TANK_AGENT_JVM_ARGS:--XX:MaxRAMPercentage=70.0} -Dnetworkaddress.cache.ttl=5 -Dnetworkaddress.cache.negative.ttl=0"
java -cp apiharness-1.0-all.jar $vm_arguments com.intuit.tank.harness.APITestHarness "$@"
echo DONE
