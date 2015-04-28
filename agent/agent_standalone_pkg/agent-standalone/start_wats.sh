#!/bin/sh
vm_arguments="-Xms4g -Xmx6g -Dnetworkaddress.cache.ttl=0";
java -cp apiharness-1.0-all.jar $vm_arguments com.intuit.tank.harness.APITestHarness "$@"
echo DONE
