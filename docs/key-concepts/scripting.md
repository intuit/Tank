# Scripting Languages

Two parts of Tank run code you write: [Logic steps](scripts.md#logic) in a script, and external [filters](filters.md). Both use the Java scripting API (JSR 223) and talk to Tank through an object called `ioBean`.

| | Logic step | External filter script |
| :- | :- | :- |
| Runs on | The agent, each time a virtual user reaches the step | The controller, when the filter is applied to a script |
| Language | JavaScript only | Chosen by the file name's extension: `.js` JavaScript, `.groovy` Groovy, `.rb` Ruby |
| Inputs (`ioBean.getInput(name)`) | `variables`, `request` (the previous request), `response` (the previous response) | `script`, the script being filtered |
| Outputs (`ioBean.setOutput(name, value)`) | `action`, which controls the flow of the test (see [Logic](scripts.md#logic)) | none; change the `script` object instead |

## Available engines

- **JavaScript** (the [Nashorn](https://github.com/openjdk/nashorn) engine) is always present, on the agent and on the controller.
- **Groovy** and **Ruby** (JRuby) are only bundled when Tank is built with the `script-engines` Maven profile:

    ```bash
    mvn clean install -P release,script-engines
    ```

    That adds them to `tank.war` (for external filter scripts) and to the `Tank-Script-Runner-all.jar` tool. The agent never has them, so Logic steps are always JavaScript.

!!! note
    The web UI tests external scripts in a sandboxed JavaScript engine, so it can only test `.js` external scripts, even on a controller built with Groovy and JRuby. Groovy and Ruby filters still run when the filter is applied.

## The `ioBean` object

| Method | Description |
| :----- | :---------- |
| `getInput(name)` | One of the inputs listed above, or `null`. |
| `setOutput(name, value)` / `getOutput(name)` | Sets or reads an output value. |
| `println(text)`, `print(text)` | Writes to the script's output. |
| `debug(text)`, `error(text)` | Writes at debug or error level. |

## Logic steps

Before a Logic step runs, Tank adds helper functions in front of it: `log`, `logWithDate`, `debug`, `error`, `toJsonObj`, `toJsonString`, `getRequest`, `getResponse`, `getResquestBody` (spelled as shown), `getResponseBody`, `getVariable`, `setVariable`, `gotoGroup`, `restartPlan`, `abortScriptGroup`, `abortScript`, `abortGroup`, `terminateUser` and `setAction`. [Logic](scripts.md#logic) describes each one. An administrator can change them in the `logic-step` section of the controller's `settings.xml`.

`getVariable` and `setVariable` work on the same [variables](functions_variables.md) as the rest of the script. The `variables` input also has an `evaluate` method, which runs `#{...}` expressions, so a Logic step can call the built-in functions:

```javascript
var vars = ioBean.getInput("variables");
var id = vars.evaluate("#{stringFunctions.getUUID()}");
setVariable("requestId", id);
```

## External filter scripts

An external filter script gets the script being filtered as `script` and edits it in place. Its list of steps can't be changed directly: copy it, change the copy and set it back.

```javascript
var script = ioBean.getInput("script");
var steps = new (Java.type("java.util.ArrayList"))(script.getSteps());
ioBean.println(script.getName() + " has " + steps.size() + " steps");
script.setSteps(steps);
```

You can develop and run filter scripts outside Tank with the Script Runner tool (`tools/script_filter_pkg/target/Tank-Script-Runner-all.jar`), which offers every language it was built with.
