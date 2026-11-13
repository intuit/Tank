# The Web UI

Tank's web UI is served at the controller's root, for example `https://tank.example.com/` (or `https://tank.example.com/tank/` if the war is deployed as `tank.war` rather than `ROOT.war`). It talks to the controller through the same [REST API](../api/index.md) that tools and CI pipelines use.

## Signing in

Sign in with your Tank user name and password, or with **Sign in with SSO** when the controller has single sign-on configured. After signing in you return to the page you asked for (the project list by default). When your session expires, Tank asks you to sign in again.

## Navigation

The menu bar runs across the top of every page:

| Section | What it's for |
| :------ | :------------ |
| **Projects** | Create and edit [projects](projects.md), and queue and run their jobs. |
| **Agent Tracker** | Watch and control running and recent [jobs](jobs.md) and their agents. |
| **Scripts** | Create, import and edit [scripts](scripts.md). |
| **Filters** | Manage [filters, filter groups](filters.md) and external filter scripts. |
| **Data files** | Upload and manage [data files](datafiles.md). |
| **Tools** | Download the desktop [tools](../supplemental-tools.md). |
| **Admin** | Users, logs and the log level. Only administrators see it; see [Administration](../administration.md). |

On the right are your user name, which opens your account page, **Help**, and **Sign out**. The Tank logo opens the home page, which outlines the path from a recording to a running test and links to each section. If the administrator has set a banner, it shows above the menu bar. The controller's version is in the footer.

## Lists and editors

The Projects, Scripts and Data files lists work the same way:

- Search, an owner filter (*My projects*, *My scripts* and so on), sorting and paging, all kept in the page address so you can bookmark or share a view.
- **Columns** to choose what shows. Column choices and widths are saved for you.
- Row buttons to open, copy, download or delete, and checkboxes to delete several items at once.
- Buttons are disabled, with a tooltip, when you don't have the right to use them.

The project, script and filter editors share these behaviors:

- Changes are kept in the page until you click **Save**. *(unsaved)* shows in the title until then, and Tank warns you before you leave with unsaved changes.
- **Revert** discards your changes; **Save as…** saves a copy under a new name.
- If someone else saved the item while you were editing it, Tank tells you and offers to reload it.
- Items you can't change open read-only.

## Tools

The **Tools** page downloads the tools that run on your own machine: the Agent visual debugger, the Script filter editor, the Tank Proxy recorder and the API test harness. They need Java 17 or later, and are only included when the controller was built with the release profile. See [Supplemental tools](../supplemental-tools.md).

## Your account

Click your user name to open your account page. There you can:

- see your user name, groups and when you last signed in;
- change your **email**;
- change your **password** (used for signing in with a user name and password, not for SSO);
- create, replace or delete your **API token**. Tools, the Jenkins plugin and scripts use it to call Tank as you, with the header `Authorization: Bearer <token>`. Tank shows a new token once, so copy it straight away;
- **Reset preferences**, which puts every table's columns and widths back to their defaults.

## Help

**Help** links to these docs, the REST API documentation (Swagger UI), and, on a release build, the guides bundled with the controller and the Javadoc for the classes external filter scripts work with. It also shows the controller's build version.

## The classic UI

The previous JSF user interface is still available under `/classic`, for example `https://tank.example.com/classic/`. It shares your session, so you don't need to sign in again. Old bookmarks to JSF pages (addresses ending in `.jsf`) redirect to the same page under `/classic`, and links from when the React UI was served under `/app` redirect to the same page at the root.

The classic UI is kept for compatibility; these docs describe the main UI.
