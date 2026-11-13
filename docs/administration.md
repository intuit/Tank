# Administration

Members of the `admin` group see **Admin** in the menu bar. The admin section has two pages: **Users** and **Logs**.

## Users

**Admin › Users** lists every user with their email, groups, API token (its last characters, if they have one) and when they last signed in. Search by name or email, sort by column and page through the list.

### Adding a user

1. Click **New user**.
2. Enter the **Name**, **Email** and a **Password** (twice).
3. Choose the user's **Groups**. The default groups are marked *(default)*.
4. Click **Create**.

A user's name can't change later: it's how Tank records what they own. Users who sign in through SSO for the first time are created automatically, with the default groups.

### Editing a user

Click **Edit** on a user's row to:

- change their email or set a new password (leave it empty to keep the current one);
- change their **groups**, which decide what they can do (see [Roles and permissions](roles.md));
- create, replace or delete their **API token**. A new token is shown once, so give it to the user straight away.

### Other actions

- **Reset preferences** puts every table back to its default columns and widths for that user.
- **Delete** removes a user. Their scripts, filters and data files are kept, with their name removed. A user who owns projects can't be deleted until the projects have been given to someone else (change each project's **Owner**) or deleted. You can't delete yourself.

### Inactive users

The controller can delete (anonymize) users who haven't signed in for a set time. It checks once, when it starts. Set these at the top level of `settings.xml`:

| Setting | Default | Description |
| :------ | :------ | :---------- |
| `user-auto-deletion-enabled` | `false` | When `false`, the controller only logs which users it would delete. |
| `user-auto-deletion-retention-days` | `730` | Days without signing in before a user is deleted. |
| `user-auto-deletion-permitted-users` | | Comma-separated user names that are never deleted. The `admin` user is never deleted. |

## Logs

**Admin › Logs** reads the controller's log files. Logs are read from the controller that answers the request, which may be one of several.

1. Choose a **log file**.
2. Set the **Initial lines** to read from the end of the file (0 reads the whole file) and how often to read new lines (**Refresh every**, in seconds; 0 reads once).
3. Click **Load log**.

The log opens in a workspace where you can:

- search events, with filters such as `level:error job:123 -logger:Hibernate`;
- show **All** levels, **Warn+** or **Errors** only;
- narrow by facets: job, agent instance, transaction, event type and logger;
- switch between a **Timeline** of events, **Patterns** (similar messages grouped, to spot repeated noise) and the **Raw** text;
- open an event to see the events around it;
- copy selected or visible events, or export them as a compact summary or JSON lines, with secrets optionally redacted, to paste into an issue or an LLM.

### Log level

**Log level** sets the controller's log level (`OFF`, `FATAL`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE` or `ALL`) for every logger. It applies only to the controller that answers, and only until it restarts. `INFO` is the default.
