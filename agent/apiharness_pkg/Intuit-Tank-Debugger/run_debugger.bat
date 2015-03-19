echo %time%
echo off
if exists "logs\*" del /q /s "logs\*"
echo on
java -cp apiharness-1.0-all.jar com.intuit.tank.harness.APITestHarness -tp=%1 -users=1 -ramp=0 -d
echo %time%