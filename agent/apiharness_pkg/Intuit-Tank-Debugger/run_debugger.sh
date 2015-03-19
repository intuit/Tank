#!/bin/sh
date
rm -f logs/*.log
echo executing command: java -jar apiharness-1.0-all.jar -users=1 -d -tp=$1
java -jar apiharness-1.0-all.jar -users=1 -d -tp=$1
date
