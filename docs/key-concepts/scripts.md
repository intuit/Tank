# Scripts

A script is the list of steps a virtual user runs during a load test. A [project](projects.md) puts scripts into script groups and test plans, and every user runs them in that order.

A step is one of:

| Step | What it does |
| :--- | :----------- |
| **Request** | Sends an HTTP or HTTPS request, then validates the response and saves values from it into variables. |
| **Think time** | Pauses for a random time between a minimum and a maximum, like a user reading a page. |
| **Sleep time** | Pauses for a fixed time. |
| **Variable** | Sets a variable to a value or a [function](functions_variables.md). |
| **Cookie** | Sets a cookie (name, value, domain and path). |
| **Authentication** | Supplies credentials (user name, password, realm, host, port and scheme: Basic, Digest or NTLM). |
| **Clear session** | Clears cookies and the cache, like closing and reopening the browser. |
| **Logic** | Runs JavaScript to change variables or control the flow of the test. |
| **Timer** | A pair of start and stop markers that time the steps between them as one transaction. |

## The script list

Open **Scripts** in the menu bar. Like the project list, it is paged, searchable (by name, product and comments), can be narrowed to one owner, and remembers your columns.

Each row can be **opened**, **copied** under a new name, downloaded as **Tank XML** (the script itself, for importing elsewhere) or as **Harness XML** (the form the agent runs), or **deleted**.

## Creating a script

There are three ways to get a script into Tank:

- Record a browser session with the [Tank Proxy](../supplemental-tools.md#tank-proxy-recorder) and create a script from the recording.
- Start a blank script and add steps by hand.
- Import a script exported from Tank.

### Importing a recorded script

1. Record a session with the Tank Proxy. It saves the traffic as XML.
2. On the Scripts page, click **New script** and choose **From a recording**.
3. Enter a **Name**, and optionally choose a **Product**.
4. Under **Recording**, choose the XML file the proxy saved (`.xml`, or gzipped `.gz`).
5. Under **Filters to apply**, tick the [filter groups](filters.md) and filters to clean up the recording with. Ticking a group ticks its filters; you can then untick any you don't want. The filters run in list order.
6. Click **Create**. Tank creates the script and opens it.

### Starting a blank script

Click **New script**, keep **Blank script**, enter a **Name** and optionally a **Product** and **Comments**, and click **Create**.

### Importing Tank XML

Click **Import Tank XML** and choose one or more scripts exported from Tank: `.xml` files, gzipped `.gz` files, or a `.zip` of them. Each file replaces the script with the ID and name it carries; a file with an ID of 0 creates a new script. If some files fail, the dialog stays open and lists which.

You need the *Create Script* right to create, copy or import scripts.

## The script editor

The editor shows the script's **Name**, **Product** and **Comments**, then its steps. As in the project editor, changes are kept in the page until you click **Save**; **Revert** discards them. **Save as…** copies the saved script under a new name (save or revert your changes first). The header also has links to download the script as Tank XML and Harness XML.

If you can't edit the script, it opens read-only.

### Working with steps

- **Add step** adds a step of the chosen type. It goes before the selected step, or at the end when none is selected.
- Click a step's **Data** cell, or its **Edit** button, to open it.
- Tick steps to **Copy** them. **Paste** puts copied steps before the selected step, in this script or another one. Copied steps last until you reload the page.
- Drag steps to reorder them, or select them and use **Move to…** to move them to a position. Scripts with more than 300 steps scroll virtually and can only be reordered with *Move to…*.
- **Timer group** wraps the selected steps in a timer. The timer's name is the logging key its timing is reported under.
- **Delete selected** removes the ticked steps. Deleting either half of a timer removes both.
- **Columns** chooses which columns show: index, group, request name, method, protocol, host, path, data, MIME type, logging key, on failure, comments, validation and assignment.

### Script tools

- **Search** finds steps by text in the parts you choose: any request field, URL, host, path, method, request name, group, logging key, MIME type, query string, post data, request and response headers and cookies, response content, think and sleep times, and variable names and values. `*` matches any text. You can then **replace** the matches, in the ticked steps, in all steps, or one step at a time with a preview. Replacing is an unsaved change like any other.
- **Apply filters** runs [filters](filters.md) over the script's steps. The result is an unsaved change.
- **Validate** checks the script: the expected time for one pass through it, the data files it reads, and warnings such as variables that are used but never set, or set but never used.

## Variables

Variables hold values used to parameterize requests, such as host names, query strings and post data. A variable can be set:

- by a **Variable** step, to a literal value or a [function](functions_variables.md);
- by a request's **assignments**, from its response;
- by a **Logic** step;
- by the [project](projects.md#variables).

Refer to a variable in a later step as `@name` (for example a host of `@{host}`). See [Functions and variables](functions_variables.md) for the full syntax.

!!! tip "Sending requests through a proxy"
    Set a variable named `TANK_HTTP_PROXY` to `proxyHost:proxyPort`, for example `myProxyServer:80`, to make the agent send requests through that proxy.

## Think time and sleep time

**Think time** simulates a user's pause between pages: Tank waits a random time between the **Minimum** and **Maximum**. **Sleep time** waits a fixed time.

Both are in milliseconds. Instead of a number you can use a variable (`@delay`), a function (`#function...`) or an expression (`#{...}`).

## Requests

A request has:

| Field | Description |
| :---- | :---------- |
| **Name** | An optional label, such as *Login*. |
| **Group** | Requests with the same group name form a group that the on-failure actions can skip or jump to. Optional. |
| **Method** | `GET`, `POST`, `PUT`, `DELETE` or `OPTIONS`. |
| **Protocol** | `http` or `https`. |
| **Host** | A host name or IP address, optionally with `:port`. It can use variables, such as `@{host}`. |
| **Path** | The path of the resource. It can use variables and functions. |
| **On failure** | What the user does when a validation fails (see below). |
| **Logging key** | Groups this request's timings in the results. |

Its tabs hold:

- **Headers**: the request headers, as key and value pairs.
- **Query string**: parameters added to the URL.
- **Post data**: the request body, as **Key-Value** pairs, **XML**, **JSON** (with a *Format JSON* button), **Plain Text**, or **Multi-Part**. A recorded multipart body is shown as recorded and can't be edited.
- **Validations**: checks on the response (see below).
- **Assignments**: values to copy from the response into variables.
- **Recorded**: what the recording captured: the cookies sent, the response headers and cookies, and the response body.

!!! note "Headers that aren't replayed"
    The agent always sends a `TEST: LoadTest` header. It doesn't replay these recorded headers: `Host`, `Cookie`, `Connection`, `Content-Type`, `Content-Length`, `If-None-Match` and `If-Modified-Since`. To send one anyway, prefix its name with `X-Include-` (for example `X-Include-If-None-Match`); the prefix is removed when the request is sent.

    Recorded request cookies are only replayed when their value is a variable (starts with `@`).

### Validations

Each validation has:

- **When**: *After the request* (the usual case) or *Before the request*.
- **Location**: the response **Body**, a **Header** or a **Cookie**.
- **Lookup**: for the body, a JSON path (such as `$.status`) or an XPath (such as `/cart/id`), depending on the **Response format** (JSON, XML or RAW); for a header or cookie, its name.
- **Operator**: *Equals*, *Not equals*, *Is empty*, *Is not empty*, *Contains*, *Does not contain*, *Less than*, *Greater than* or *Equals any*.
- **Value** to compare with, where the operator needs one.

A lookup of `HTTPRESPONSECODE` checks the HTTP status code (such as `200` or `404`), and `HTTPRESPONSEMESSAGE` the status message (such as `OK` or `Not Found`).

### Assignments

Each assignment has a **Location** (Body, Header or Cookie), the **Variable** to set, and the **Lookup** to read, as for validations.

### On failure

When a validation fails, the user does one of:

| Action | Description |
| :----- | :---------- |
| **Abort script, goto next script** (default) | Skips the rest of the script and goes to the next script in the project. |
| **Abort script group goto next script group** | Skips to the next script group. |
| **Skip remaining requests in group** | Goes to the step after the last request in this request's group. |
| **Continue to next request** | Carries on. |
| **Goto specified group** | Jumps to the first step of the group you name. |
| **Terminate user** | Stops this virtual user. |
| **Restart user** | Starts this user's test plan again. |

## Clear session

Clears the user's cookies and cache, as if the browser were closed and reopened.

## Logic

A Logic step runs JavaScript on the agent, between requests. Use it to change variables, control the flow of the test, or do anything too complex for the other steps. The editor's **What can a script use?** button shows the code Tank runs before your script.

A Logic step can read:

- **variables**: the current user's variables;
- **request**: the previous request;
- **response**: the previous response.

It controls the flow by setting the `action` output on `ioBean`, which the functions below do for you:

| Action | Effect |
| :----- | :----- |
| `goto <groupName>` | Jumps to the group with that name in the script. |
| `restartPlan` | Restarts the test plan from the start. |
| `abortScriptGroup` | Aborts the current script group and goes to the next one. |
| `abortScript` | Aborts the current script and goes to the next one. |
| `abortGroup` | Aborts the current group of requests and goes to the request after it. |
| `terminateUser` | Stops the current user. |

**Test it** runs your script on the controller against test data you enter (variables, a request and a response), or against the step's previous request with **Use the previous request**, and shows the result. See [Scripting languages](scripting.md) for more on the `ioBean` object.

These functions are added in front of every Logic step. An administrator can change them in the `logic-step` section of the controller's `settings.xml`.

??? abstract "Functions added to every Logic step"

    === "Logging"
        ```js
        /** Prints the line, with the date, to the output. */
        function logWithDate(line) {
            ioBean.println(new Date().toString + ": " + line);
        }

        /** Prints the line to the output. */
        function log(line) {
            ioBean.println(line);
        }

        /** Prints the line at error level. */
        function error(line) {
            ioBean.error(line);
        }

        /** Prints the line at debug level: shown when testing, not logged in production. */
        function debug(line) {
            ioBean.debug(line);
        }
        ```

    === "Data transformation"
        ```js
        function toJsonObj(text) {
            try {
                return JSON.parse(text);
            } catch(e) {
                error("Error parsing json: " + e);
            }
            return text;
        }

        function toJsonString(jsonObj) {
            try {
                return JSON.stringify(jsonObj, null, '\t');
            } catch(e) {
                error("Error converting json to string json: " + e);
            }
            return jsonObj;
        }
        ```

    === "Data retrieval"
        ```js
        /** The com.intuit.tank.http.BaseRequest of the last call, or null if none was made. */
        function getRequest() {
            return ioBean.getInput("request");
        }

        /** The com.intuit.tank.http.BaseResponse of the last call, or null if none was made. */
        function getResponse() {
            return ioBean.getInput("response");
        }

        /** The request body as a string, or "" if there is none. (The name is spelled as shown.) */
        function getResquestBody() {
            if (getRequest() != null) {
                return getRequest().getBody();
            }
            return "";
        }

        /** The response body as a string, or "" if there is none. */
        function getResponseBody() {
            if (getResponse() != null) {
                return getResponse().getBody();
            }
            return "";
        }

        /** The variable's value, or null if it doesn't exist. */
        function getVariable(key) {
            return ioBean.getInput("variables").getVariable(key);
        }

        /** Sets the variable. */
        function setVariable(key, value) {
            ioBean.getInput("variables").addVariable(key, value);
        }
        ```

    === "Flow control"
        ```js
        /** Jumps to the group with this name; if there is none, skips to the next script. */
        function gotoGroup(groupName) {
            setAction("goto " + groupName);
        }

        /** Restarts the test plan from the start. */
        function restartPlan() {
            setAction("restartPlan");
        }

        /** Aborts the current script group and goes to the next. */
        function abortScriptGroup() {
            setAction("abortScriptGroup");
        }

        /** Aborts the current script and goes to the next. */
        function abortScript() {
            setAction("abortScript");
        }

        /** Aborts the current group and goes to the request after it. */
        function abortGroup() {
            setAction("abortGroup");
        }

        /** Stops this user. */
        function terminateUser() {
            setAction("terminateUser");
        }

        /** Sets the action output. */
        function setAction(action) {
            ioBean.setOutput("action", action);
        }
        ```
