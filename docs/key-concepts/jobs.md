# Jobs and the Agent Tracker

A **job** is one run of a [project](projects.md). Queueing a job takes a snapshot of the project, so you can keep editing the project without changing a job that is queued or running. When the job runs, the controller starts **agents**, each running a share of the virtual users, and they report back to the controller as the test runs.

## Running a test

1. In the project editor, set the job settings on the [Create job](projects.md#create-job) tab.
2. Click **Validate and queue…**, check the summary, errors and warnings, and click **Queue job**. The job is created with the status *Created* and appears on the **Job queue** tab.
3. Click the job's **Run** button. The controller launches the agents (EC2 instances, or the registered agents of a standalone controller) and the job becomes *Starting*, then *Running* once the agents report in.
4. With **Two-step start**, the agents start and wait. Once they are connected and ready, click **Start load** to begin the test.

You can queue, run and control a project's jobs if you own the project or have the *Control Job* right (see [Roles and permissions](../roles.md)).

## The Agent Tracker

**Agent Tracker** in the menu bar shows the jobs of every project with activity in the last seven days, grouped by project. A project's **Job queue** tab shows the same view for that project only.

The toolbar has:

- **View**: how to look at the jobs (below). Your choice is remembered in this browser.
- **Show finished jobs**: include jobs that have ended.
- **Refresh** now, and **Refresh every** 10, 15, 30, 60 or 120 seconds, or off.
- **Columns** (table view): show or hide columns.

### Views

| View | Shows |
| :--- | :---- |
| **Table** | A tree of projects, their jobs and each job's agents. Columns: name, ID, status, WebSocket state, region, active users, target users, TPS, failures, CPU, memory, heap, started and completed. |
| **Heat map** | Every agent as a tile, job by job and region by region, shaded by the figure you pick (TPS, active users, failures, time since last seen, CPU, memory, heap or JVM threads), so a slow, failing or silent agent stands out. A tile opens that agent's figures and actions. |
| **Metrics** | Agent host and JVM health from Amazon CloudWatch: fleet averages, a CPU chart of the busiest agents, and a row per agent with CPU, memory, heap, JVM thread, GC and TCP connection charts. |
| **Jobs** | A card per job: users against target, TPS and its trend, failures, its agents at a glance, and its actions. |
| **Regions** | Agents by AWS region and availability zone: how many are running, paused, starting, lost or finished, with their users, TPS and jobs. |
| **Timeline** | Jobs as bars from start to end over the last 6 hours, 24 hours or 7 days, coloured by status, so overlapping runs and gaps are easy to see. |

CPU, memory and JVM figures come from CloudWatch, where the controller collects them; agents that don't report them show *n/a*.

### Job details and charts

The **chart** button on a job (or a job's name) opens its details: status, who created it and when, start and end times, active and total users, and failure counts (kills, aborts, restarts, gotos, skips and group skips). It also shows the job's settings as they were queued, and charts of **active users by script** and **TPS** over time, including the TPS of each request. For a single agent, the chart button shows that agent's TPS.

A project's row has a button to download its **Harness XML**, the file the agents run.

## Controlling a job

The buttons on a job or agent row change with its state. Only the actions the job or agent can take at the moment are shown.

| Action | Applies to | What it does |
| :----- | :--------- | :----------- |
| **Run** | job | Starts a job that is *Created*. |
| **Start load** | job | For a two-step job that is *Starting*: begins the load once the agents are ready. |
| **Pause ramp** | job, agent | Stops adding users and holds the current level; users already running carry on. |
| **Resume ramp** | job, agent | Continues adding users. |
| **Pause** | job, agent | Stops adding users and interrupts the running users. |
| **Resume** | job, agent | Lets users run and ramp again. |
| **Stop** | job, agent | Asks users to finish and stop, at the point set by the job's **Stop behavior**: after the current step, script, script group, or the whole test. |
| **Kill** | job, agent | Terminates the agents immediately, without letting users finish. Tank asks you to confirm. |
| **Delete** | job | Removes a job that hasn't started (*Created*). |

Actions are sent to the agents asynchronously, so the status can take a few seconds to change. The tracker refreshes straight after an action, and again shortly after.

## Job states

| Status | Meaning |
| :----- | :------ |
| **Created** | Queued and waiting to be run. |
| **Starting** | Agents are being launched or, for a two-step job, are waiting for *Start load*. |
| **Running** | The test is running. |
| **RampPaused** | Running, but no more users are being added. |
| **Paused** | Paused. |
| **Stopped** | Stopped by a user. |
| **Completed** | Finished, or killed. |
| **Aborted** | Couldn't start, for example when a standalone controller has no agents available. |
| **Deleted** | Deleted before it ran. Deleted jobs aren't shown. |

An agent's status (such as *pending*, *starting*, *ready*, *running*, *stopping*, *terminated*) and its WebSocket state are shown on its row. An agent that has disconnected mid-run is shown as lost.

If agents don't start or report in time, the controller's watchdog restarts them, up to a limit the administrator sets (the `watchdog` section of `settings.xml`); after that it kills the job.

## Results

While a job runs, the Agent Tracker shows live active users, TPS and failures for each project, job and agent, and the job details show users and TPS over time.

Agents also send their TPS and per-request timing results to the reporting provider configured on the controller (the `reporting` section of `settings.xml`). The default configuration writes them to Amazon CloudWatch, under the `Intuit/Tank` namespace, where you can chart and keep them. A request's **logging key** decides which name its timings are reported under, and a [timer](scripts.md#working-with-steps) reports a set of steps as one transaction. The job's **logging profile** controls how much the agents log about each request.

## Automating jobs

Everything here is also available through the [REST API](../api/index.md). Create an API token on your [account page](web-ui.md#your-account) to queue, start, check and stop jobs from CI.
