#!/bin/sh                                                                                                                                                                                                                                    
vm_arguments="-Dnetworkaddress.cache.ttl=5 -Dnetworkaddress.cache.negative.ttl=0";
java -cp apiharness-1.0-all.jar $2 $vm_arguments com.intuit.tank.harness.APITestHarness $1
echo DONE
