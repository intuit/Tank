# Supplemental Tools

Tank ships four tools that run on your own machine, alongside the controller:

| Tool | File | What it does |
|---|---|---|
| [Agent Visual Debugger](#agent-visual-debugger) | `Tank-Debugger-all.jar` | Steps through a script request by request. With `--headless`, replays a project from the command line |
| [Script Filter Editor](#script-filter-editor) | `Tank-Script-Runner-all.jar` | Writes and tests external (JavaScript) filters against a Tank XML script |
| [Tank Proxy](#tank-proxy-recorder) | `Tank-Proxy-pkg.zip` | Records a browser session as a Tank XML script to upload |
| [API Test Harness](#api-test-harness) | `apiharness-1.0-all.jar` | The agent's harness, for running a harness XML file locally from the command line |

All of them need Java 21 or later. Each one is built with the controller that serves it, so download them from the controller you use.

## Downloading the tools

### From the web UI
Open the `Tools` page. It lists each tool with the controller's version and build date, and a download link for each.

!!! note "Release builds only"
    The controller has the tools only when it was built with `mvn clean install -P release`. A development build has none: the download returns 404, and the Tools page in the new UI shows "Not included in this build".

### With an API token
Scripts and CI jobs can download each tool from the [REST API](api/index.md) with a user's API token, instead of a web login:

| Tool | Endpoint | File |
|---|---|---|
| Agent Visual Debugger | `GET /v2/tools/debugger` | `Tank-Debugger-all.jar` |
| Script Filter Editor | `GET /v2/tools/script-runner` | `Tank-Script-Runner-all.jar` |
| Tank Proxy | `GET /v2/tools/proxy` | `Tank-Proxy-pkg.zip` |
| API Test Harness | `GET /v2/tools/api-harness` | `apiharness-1.0-all.jar` |

```bash
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" -o Tank-Debugger-all.jar <tank_url>/v2/tools/debugger
```

| Status | Meaning |
|---|---|
| 200 | The file |
| 401 | No user is signed in, or the token is invalid |
| 403 | The agent token was used. These endpoints need a user's token or session |
| 404 | The controller was built without `-P release`, so it has no tools |

Create your API token on your account page in the web UI (see [Authentication](api/index.md#authentication)).

## Agent Visual Debugger
`Tank-Debugger-all.jar` runs a script on your machine one request at a time, showing each request, response and variable as it goes.

Start it by double-clicking the jar, or from the command line. On the command line you can give the controller URL, and optionally your API token, as positional arguments:

```bash
java -jar Tank-Debugger-all.jar                                  # pick the Tank instance later
java -jar Tank-Debugger-all.jar https://tank.example.com      # connect to this controller
java -jar Tank-Debugger-all.jar https://tank.example.com <token>
```

### Using the debugger
- **Connect to a Tank:** `File > Select Tank Instance...` asks for the base URL and your API token. It checks the connection before using it.
- **Load a script:** open a file from disk, or use `Choose Script` or `Choose Project` to load one from the connected controller. `Reload` fetches it again. `Choose Datafile` and `Show Project Variables` supply the data a project's script uses.
- **Run:** `Start`, `Next Step`, `Skip`, `Run to Breakpoint...`, `Pause` and `Stop` are on the toolbar, the `Run` menu and the script's right-click menu. Set breakpoints and skipped steps with `Toggle Breakpoint` and `Toggle Skip Step` on the right-click menu, and clear them all with `Clear All Breakpoints` and `Clear All Skips`.
- **HTTP client:** pick which agent HTTP client runs the requests. The list comes from the connected controller.
- **Sleep and think steps:** the `Run Sleep Steps` and `Run Think Steps` check boxes decide whether timing steps actually wait. Both are off by default.
- **Output:** the `Actions` menu saves the requests and responses (`debuggerOutput.txt`) or the log (`debuggerLog.txt`), and clears the log output.

### debugger.properties
On first use the debugger writes `debugger.properties` to the directory you start it from. Edit it to list the controllers in the `Select Tank Instance` drop-down and to set the timing-step defaults:

```properties
# One line per controller: tank.instance.<name>=<base url>
tank.instance.TANK=http://tank-url

# Set to true to run sleep/think steps, false to skip them
run.sleep.steps=false
run.think.steps=false
```

When you connect to an instance from the list with a token, the debugger saves that token in the same file as `tank.token.<name>`. Keep the file private.

### Headless mode
With `--headless`, the same jar replays a Tank project from the command line, with no window. It is meant for Jenkins jobs and scripts. It writes the `authid` and the result of each step to files in the current directory.

```bash
java -jar Tank-Debugger-all.jar --headless -h=<controller_base_url> -p=<projectId> -t=<token>
```

| Option | Meaning |
|---|---|
| `--headless` | Run from the command line. Without it, the visual debugger opens |
| `-h`, `--host` | Tank controller URL, for example `https://tank.example.com` (required; include the context path, such as `/tank`, if the war isn't deployed as `ROOT.war`) |
| `-p`, `--project` | Id of the Tank project whose script to run (required) |
| `-t`, `--token` | Tank API token (required) |
| `--help` | Usage and exit codes |
| `--version` | Build version and git commit |

The exit code reports whether the run finished. It does not validate the result: read `output.log` for that.

| Code | Meaning |
|---|---|
| 0 | The run finished |
| 1 | The script failed the hostname replacement check (not filtered for production) |
| 2 | Invalid command line arguments |
| 3 | Could not get the project or its script from Tank |
| 4 | The run could not be started or did not finish |

Output files, written to the current working directory:

- `output.log`: the result summary, one `key,value` per line. The line `authid,<value>` is the authid. Other lines: `project`, `project_id`, `username`, `srsversion`, `total_steps`, `total_executed`, `execution_percentage`, `taxhub_flow`, `filing_status`, `timestamp`. Failed steps also log the full request, response and variables.
- `validation-report.json`: every step with its pass or fail result and validation errors.
- `debugger.log`: general debug log.

The machine must reach the Tank controller over HTTP(S), and the hosts the replayed script calls.

## Script Filter Editor
`Tank-Script-Runner-all.jar` (window title "Intuit Tank Script Filter Editor") is for writing and testing external filters: scripts that change a Tank script's steps when an internal [filter](key-concepts/filters.md) can't express the change.

```bash
java -jar Tank-Script-Runner-all.jar
java -jar Tank-Script-Runner-all.jar https://tank.example.com <token>
```

The optional positional arguments are the controller URL (default `http://localhost:8080/`) and your API token. The controller is only used in remote storage mode.

To test a filter:

1. `Select Tank XML` > `Select File...` loads a script exported from Tank (`GET /v2/scripts/download/{scriptId}` returns this format).
2. Choose the script language, write the filter in the editor, and click `Run Script`. The filter reads the loaded script with `ioBean.getInput("script")`, the same input the controller gives external filters when it applies them, and can log with `ioBean.println`, `ioBean.debug` and `ioBean.error`. Output appears in the lower pane.
3. `Show XML` displays the script after the filter ran; `Save XML` writes it to a file.

`Open Script...`, `Save Script...` and `Save Script As...` work on the local disk in local storage mode (the default). `Toggle Storage Mode` switches to remote mode, where they list and save the controller's external scripts instead.

!!! note "Languages"
    JavaScript (Nashorn) is always included. Groovy and Ruby (JRuby) appear in the language list only when the jar was built with `mvn clean install -P release,script-engines`.

## Tank Proxy (recorder)
The Tank Proxy records the traffic of a browser session and saves it as Tank XML, which you then upload as a new script (see [Importing a recorded Script](key-concepts/scripts.md#importing-a-recorded-script)).

`Tank-Proxy-pkg.zip` unzips to an `Intuit-Tank-Proxy` directory containing:

| File | Purpose |
|---|---|
| `Tank-Proxy-all.jar` | The recording proxy |
| `startProxy.sh` | Runs `java -jar Tank-Proxy-all.jar` |
| `recording-proxy-config-default.xml` | Default settings: port `8888`, output file `outputFile.xml`, and image and PDF response bodies excluded |
| `auto_generated_ca.p12`, `auto_generated_ca.pem` | The certificate authority the proxy signs HTTPS certificates with |
| `readme.txt` | Setup notes |

Run it from that directory, so it finds the configuration and the CA files:

```bash
cd Intuit-Tank-Proxy
java -jar Tank-Proxy-all.jar
```

### Recording a session
1. Trust the proxy's CA in your browser or operating system: import `auto_generated_ca.pem` as a trusted certificate authority ("OWASP Custom CA for Tank"). On Windows you can import the `.p12` from the command prompt with `certutil -f -p password -importpfx auto_generated_ca.p12`. If the `.p12` is missing from the working directory, the proxy generates a new CA and writes a new `auto_generated_ca.pem`, which you must trust instead.
2. Set your browser's HTTP and HTTPS proxy to `localhost` and the proxy port (8888 by default).
3. Optionally open `File > Settings` to change the port, the output file and the request and body inclusion and exclusion rules. The settings dialog can save and open them as XML files.
4. Click `Start Recording` (also in the `Session` menu). `Pause Recording` and `Stop Recording` are next to it.
5. Exercise the application in the browser. Each transaction appears in the table; use the filter box to search them and `Delete Selected` to drop ones you don't want.
6. Click `Stop Recording`, then `Save` (`File` menu). `File > Open Recording...` reopens a saved recording, and `Session > Hosts...` lists the hosts that were recorded.
7. Upload the saved XML to Tank as a new script.

## API Test Harness
`apiharness-1.0-all.jar` is the harness the agents run. Locally it runs a harness XML file (which you can download with `GET /v2/projects/download/{projectId}` or `GET /v2/scripts/harness/download/{scriptId}`) from the command line, which helps when you debug a script:

```bash
java -jar apiharness-1.0-all.jar -tp=<test plan file> [options]
```

| Option | Meaning |
|---|---|
| `-tp=<file>` | The harness XML file to run. Several files can be given, separated by commas |
| `-users=<n>` | Number of users to run concurrently |
| `-start=<n>` | Number of users to start with |
| `-ramp=<minutes>` | Time to get from the start users to the full number of users |
| `-time=<minutes>` | Length of the simulation |
| `-d` | Debug: step through each request |
| `-t` | Trace: print each request |

The harness's other options (`-http`, `-token`, `-jobId` and so on) are how a controller starts an agent, and are not needed for a local run.

## Jenkins plugin (not built)
`tools/jenkins_plugin` holds the source of an old Jenkins plugin that started a Tank Proxy before a build and stopped it afterwards. It is not part of any build (its module is commented out in `tools/pom.xml`) and is not shipped. To run Tank from Jenkins, download the tools with an [API token](#with-an-api-token) and use the debugger's [headless mode](#headless-mode) or the [REST API](api/index.md).
