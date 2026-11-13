# Glossary

Agent
:   An instance, usually an EC2 instance started for the job, that runs a share of a job's virtual users and reports back to the controller. Agents exist for the length of a test.

Agent Tracker
:   The page that shows running and recent jobs and their agents, and controls them. See [Jobs](jobs.md).

API token
:   A secret that lets tools and scripts call the REST API as you. Created on your account page.

Assignment
:   A rule on a request that copies a value from the response (body, header or cookie) into a variable.

Controller
:   The central server: it serves the web UI and the REST API, stores projects and scripts in its database, and starts and coordinates agents.

Data file
:   A file of test data, usually CSV, that scripts read with `ioFunctions`. Its lines are divided among a job's agents. See [Data files](datafiles.md).

External filter
:   A filter that runs a script (an *external script*) to change a script's steps. See [Filters](filters.md#external-filters).

Filter
:   A rule, or a script, applied to a recorded script to remove, change or add to its requests. See [Filters](filters.md).

Filter group
:   A named set of filters applied together.

Group
:   (Of users) A role, such as *user* or *project-manager*, that grants rights. See [Roles and permissions](../roles.md). (Of requests) A label shared by requests in a script, which on-failure actions and Logic steps can skip or jump to.

Harness XML
:   The form of a script or project that agents run. Downloadable from the Scripts page and the Agent Tracker.

Job
:   One run of a project: a snapshot of the project, queued, then run on agents. See [Jobs](jobs.md).

Logging key
:   The name a request's (or a timer's) timings are reported under.

Logic step
:   A script step that runs JavaScript to change variables or control the flow of the test.

Project
:   The definition of a load test: users and times, test plans, data files, variables and job settings. See [Projects](projects.md).

Ramp time
:   How long a test takes to reach full load.

Script
:   The list of steps a virtual user runs. See [Scripts](scripts.md).

Script group
:   An ordered, looped set of scripts within a test plan. It is a self-contained flow for a user, and a point where a stopped user can finish.

Simulation time
:   How long a test runs.

Stop behavior
:   Where users finish when a job is stopped: after the current step, script, script group, or the whole test.

Tank Proxy
:   The recording tool: a proxy that records a browser session as XML to create a script from.

Test plan
:   Part of a project that runs a percentage of the users, made of script groups.

Timer
:   A pair of script steps that times the steps between them as one transaction.

TPS
:   Transactions (requests) per second.

Two-step start
:   A job setting that starts every agent first and waits for *Start load* before applying load.

Validation
:   A check on a request's response. When it fails, the request's on-failure action applies.

Variable
:   A named value used in steps, set by a Variable step, an assignment, a Logic step or the project.

Virtual user
:   One simulated user, running a test plan's scripts on an agent.
