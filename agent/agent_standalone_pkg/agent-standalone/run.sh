#!/bin/bash
controller="[controllerUrl]"
host=`hostname`
capacity=4000
nohup java -jar agent-standalone-all.jar -controller=$controller -host=$host -capacity=$capacity &