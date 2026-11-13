Intuit Tank Agent Standalone
--------------------------------------
A standalone agent runs tests for a controller in standalone mode (<standalone>true</standalone>
in its settings.xml), on a machine you start yourself instead of an EC2 instance the controller launches.

To deploy,
1. Put these files on the machine that will serve as a standalone agent. It needs Java 21 or later.
2. Edit run.sh: set controller to the controller's base url and token to the agent-token from the
   controller's settings.xml. Optionally change host (how the controller reaches this machine) and capacity.
3. Start the agent with ./run.sh. It listens for the controller on port 1090, and the test harness it starts
   listens on the agent-port from settings.xml (8090 by default).

When a job starts, the agent downloads settings.xml and the harness from the controller into this directory
and runs startAgent.sh. Set TANK_AGENT_JVM_ARGS to change the harness JVM options (default -XX:MaxRAMPercentage=70.0).

Documentation: https://intuit.github.io/Tank/installation-guide/installation/
