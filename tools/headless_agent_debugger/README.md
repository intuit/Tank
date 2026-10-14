# Headless Debugger

Replays a Tank project from the command line. It writes the `authid` used to pull profile data from Splunk, and the result of each step.

## Get it

Download it from the controller: `GET <tank_url>/tools/Headless-Debugger-all.jar` (listed on the Tank Tools page). The jar matches the Tank version that served it. Check with `--version`.

## Run

```
java -jar Headless-Debugger-all.jar -h=<controller_base_url> -p=<projectId> -t=<token>
```

| Option | Meaning |
|---|---|
| `-h`, `--host` | Tank controller URL, for example `https://tank.example.com/tank` |
| `-p`, `--project` | Id of the Tank project to run |
| `-t`, `--token` | Tank API token |
| `--help` | Usage and exit codes |
| `--version` | Build version and git commit |

## Exit codes

The exit code reports whether the run finished. It does not validate the result: read `output.log` for that.

| Code | Meaning |
|---|---|
| 0 | The run finished |
| 1 | The script failed the hostname replacement check (not filtered for production) |
| 2 | Invalid command line arguments |
| 3 | Could not get the project or its script from Tank |

## Output files

Files are written to the current working directory.

- `output.log`: the result summary, one `key,value` per line. The line `authid,<value>` is the authid. Other lines: `project`, `project_id`, `username`, `srsversion`, `total_steps`, `total_executed`, `execution_percentage`, `taxhub_flow`, `filing_status`, `timestamp`. Failed steps also log the full request, response and variables.
- `validation-report.json`: every step with its pass or fail result and validation errors.
- `debugger.log`: general debug log.

## Network

The machine must reach the Tank controller over HTTP(S). The replayed script then calls the hosts in the script, so those must be reachable too.
