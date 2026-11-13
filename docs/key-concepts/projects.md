# Projects

A project describes a load test: how many virtual users to run, how fast to add them and for how long, which [scripts](scripts.md) they run, which [data files](datafiles.md) and variables the scripts can use, and how the test's agents are set up. You run a project by queueing a **job** from it; see [Jobs and the Agent Tracker](jobs.md).

A project holds:

```text
Project
├── Users and times      workload type, ramp, simulation time, users by region
├── Test plans           each runs a share of the users
│   └── Script groups    run in order, each looped
│       └── Scripts      run in order, each looped
├── Data files
├── Variables
└── Job settings         logging, HTTP client, stop behavior, agents
```

## The project list

Open **Projects** in the menu bar. The list is paged and sorted on the server, and its state (page, sort, search) is kept in the address, so a reload or the browser's Back button brings you back to the same view.

- **Search** matches the name, product and comments.
- **All owners** narrows the list to one owner; **My projects** is your own.
- **Columns** shows or hides columns. Column choices and widths are saved for you; reset them on your [account page](web-ui.md#your-account).
- Each row can be **opened**, **copied** under a new name, or **deleted**. Tick several rows to delete them together.

## Creating a project

1. Click **New project**.
2. Enter a **Name** (it must be unique), and optionally choose a **Product** and add **Comments**. Products are a list the administrator configures, used to categorize projects and scripts.
3. Click **Create**. Tank creates the project with one test plan, **Main**, which runs 100% of the users, and opens it.

You need the *Create Project* right to create or copy a project (see [Roles and permissions](../roles.md)).

## The project editor

The top of the editor has the project's **Name**, **Product**, **Owner** and **Comments**. Only the owner or an administrator can change the owner.

Below them are six tabs: **Users and times**, **Scripts**, **Data files**, **Variables**, **Create job** and **Job queue**. Changes to the first five are kept in the page until you click **Save**; the title shows *(unsaved)* until then, and Tank warns you if you try to leave with unsaved changes.

- **Save** checks the project first. If something is wrong, the problems are listed and the tabs that have them are marked with a count.
- **Revert** throws away unsaved changes.
- **Save as…** saves the project under a new name, with your unsaved changes; the original stays as it was last saved.
- The **trash** button deletes the project.

If someone else saved the project while you were editing it, Tank tells you and offers to reload it.

If you can't edit the project, it opens read-only with a note saying so.

### Users and times

| Setting | Description |
| :------ | :---------- |
| **Workload type** | **Increasing (linear user ramp)** adds users in steps over the ramp time, then holds them. **Non-linear (target users per second)** injects new users at a rate that rises from 0 to a target rate over the ramp time. |
| **Simulation time** | How long the test runs. Required when *Run scripts until* is *Simulation time is reached*. |
| **Ramp time** | How long it takes to reach full load. |
| **Initial users** | Users started as soon as the test starts. Usually 0. |
| **User increment** | *(Increasing only)* Users added at each step of the ramp. Usually 1. With 3,000 users, a 15 minute ramp and an increment of 1, about 3.33 users are added each second. |
| **Run scripts until** | *(Increasing only)* **Simulation time is reached**, or **Scripts finish**: each user runs its test plan, with its loops, once. |
| **Target users per second** | *(Non-linear only)* The rate new users are injected at by the end of the ramp. |
| **Users by region** | *(Increasing)* The number of users to run in each region. **Total users** adds them up. |
| **Share of users by region** | *(Non-linear)* The percentage of users each region runs. |

The regions are the AWS regions the administrator has configured. On a standalone controller there is a single, standalone region.

Times are written with `d`, `h`, `m`, `s` and `ms`, for example `1h 30m` or `90m`. A time can also refer to the other times: `RT` (ramp time), `ST` (simulation time) and `ET` (execution time), as in `3ET - .5RT`. A region's users can be an expression too, such as `4 * RT / 1000` for four users a second. Separate math symbols with spaces.

### Scripts: test plans and script groups

The **Scripts** tab sets the flow of the test.

- A **test plan** runs a percentage of the test's users. Every project has at least one. Tank warns you if the percentages don't add up to 100%.
- A test plan contains **script groups**, which run in order. A script group is a self-contained flow for a user and has its own **Loop** count. Script groups are also where a stopped user can finish (see *Stop behavior* below).
- A script group contains **scripts**, which run in order, each with its own **Loop** count.

To work with them:

- **Add test plan** adds a plan with a name and user percentage; each plan is a tab. **Delete test plan** removes a plan and its groups.
- **Add script group** adds a group at the end; the **insert** button on a group's row adds one before it. Each row can also be edited or deleted.
- In the script group dialog, set the group's **Name** and **Loop**, find scripts under **Add scripts** (by name, product or comments) and click **Add**. Use the arrows to reorder the scripts, set each script's **Loop**, or remove it. Click **Done**, then save the project.

!!! example "Loops"
    *Script Group 1* loops twice and contains *Script1* (looped once) and *Script2* (looped twice). Each user runs:
    `Script1, Script2, Script2, Script1, Script2, Script2`

### Data files

The **Data files** tab lists the [data files](datafiles.md) the project's scripts can read.

- Find a file under **Add data files** and click **Add**.
- **Upload data files** uploads new files and adds them to the project.
- The **eye** button previews a file; the **remove** button takes it out of the project (the file itself is kept).

When a job runs, each data file's lines are divided evenly among the job's agents. If the job has exactly one data file, it is the default file that `#{ioFunctions.getCSVData(column)}` reads.

### Variables

Project variables are set for every user of every script in the project. Click **Add variable** and enter a name and value. Each name can be used once.

By default a script can't change a project variable. Turn on **Let scripts override project variables** to let scripts set their own values.

!!! tip "Sending requests through a proxy"
    Set a variable named `TANK_HTTP_PROXY` to `proxyHost:proxyPort`, for example `myProxyServer:80`, to make the agents send requests through that proxy.

See [Functions and variables](functions_variables.md) for how scripts use variables.

### Create job

The **Create job** tab holds the settings a job is made with, and queues the job. The settings are saved with the project.

| Setting | Description |
| :------ | :---------- |
| **Job name** | Leave it blank for the default name. |
| **Logging profile** | How much the agents log about each request: **Terse**, **Standard**, **User Variables** (standard plus user variables), **Verbose** (every field) or **Trace Calls** (every call). |
| **HTTP client** | The HTTP client implementation the agents use to drive load. The administrator configures the choices. |
| **Stop behavior** | Where users finish when the job is stopped: after the current **Step**, **Script**, **Script Group**, or the whole **Test**. |
| **Location** | Where the test runs from, for information. The administrator configures the choices. |
| **Agent instance type** | The EC2 instance type for the agents. Choosing a type fills in its default users per agent. Not shown on a standalone controller. |
| **Users per agent** | *(Increasing)* How many users each agent runs, which decides how many agents the job starts. |
| **Target rate per agent** | *(Non-linear)* How many users per second each agent injects. |
| **Assign Elastic IPs** | Gives each agent an unassigned Elastic IP in its region. Not shown on a standalone controller. |
| **Two-step start** | Launches every agent first and waits for **Start load**, so load begins at an exact moment, for example in a spike test. Send *Start load* within 5 to 10 minutes of the agents being ready. |

Click **Validate and queue…**. Unsaved changes are saved first. Tank then shows what the job will be (users, ramp time, simulation time and expected run time) with any errors to fix and warnings to check. If the job is valid, click **Queue job**. The job is a snapshot of the project as it is now, so later edits to the project don't change it. Tank then opens the **Job queue** tab.

You need the *Control Job* right, or to own the project, to queue a job.

### Job queue

The **Job queue** tab shows this project's jobs and their agents, and lets you run, pause, stop or kill them. It is the same view as the [Agent Tracker](jobs.md), limited to one project.
