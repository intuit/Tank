# Filters and Filter Groups

A recording captures everything the browser did, including analytics calls, static files and values that only made sense once. **Filters** clean up and enhance a recorded [script](scripts.md) so it behaves more like a real user: they can remove requests, rewrite hosts and headers, add think times, validations and assignments, and more.

A **filter group** is a named set of filters to apply together, such as every filter for one product or environment.

Filters run when you:

- create a script from a recording, by ticking filters or groups under **Filters to apply** (see [Importing a recorded script](scripts.md#importing-a-recorded-script)); ticking a group ticks all its filters, and you can untick the ones you don't need;
- click **Apply filters** in the script editor, which changes the open script without saving it.

Filters run in the order of the filter list.

## The Filters page

Open **Filters** in the menu bar. The page has two tables, **Filter groups** and **Filters**. Each can be searched by name or ID, narrowed to a product, and sorted.

- In the groups table, the count in a group's **Filters** column shows that group's filters in the filters table. The filters table can also be narrowed to one group.
- Each row can be **opened**, **copied** under a new name, or **deleted**. Tick several rows to delete them together.
- **External filters** opens the list of external scripts (see below).

## Creating a filter

1. Click **New filter**.
2. Enter a **Name**, and optionally choose a **Product**.
3. Choose the **Filter type**: **Internal** (rules you build here) or **External** (a script).
4. Build the rules, or choose the external script, and click **Create**.

As in the other editors, **Save**, **Revert** and **Save as…** work on the whole filter, and the owner or an administrator can change the **Owner**.

### Internal filters

An internal filter is a set of **conditions** (*If*) and **actions** (*Then*). For each request in the script, the filter checks the conditions, and if they match, applies the actions to the request.

**If *All* / *Any*** of these conditions match a request: choose whether every condition must match, or at least one.

Each condition has:

- the **part** of the request to look at: **Hostname**, **Path**, **Query String** or **Post data**;
- the **test**: **Contains**, **Does not contain**, **Starts with**, **Matches** (a regular expression), **Exist** or **Does not exist**;
- the **value** to test for.

Each action has a **type**, a **part** of the request to act on, and the inputs that part needs (a key, a value, or both):

| Type | Parts it acts on |
| :--- | :--------------- |
| **Remove** | the whole request, a request header, a query string parameter, a request cookie, or post data |
| **Replace** | a request cookie, request header, query string parameter or post data value; a validation; an assignment; the on-failure action; the path; the host |
| **Add** | a response data check, a validation, an assignment, a think time, a sleep time, a request cookie, a request header, a query string parameter, or post data |

For validation actions, choose the check (*Equals*, *Not equals*, *Contains* and so on) before the value. For the on-failure action, choose one of the [on-failure actions](scripts.md#on-failure), apart from *Goto specified group*, which filters can't use.

!!! example
    A filter with the condition *Hostname* *Contains* `doubleclick` and the action *Remove* the request removes every request to DoubleClick from a recording.

### External filters

An external filter runs a script, for changes an internal filter can't express. Choose the script under **External script**; **Edit script** opens it.

External scripts are listed under **Filters › External filters**. There you can create, open, download and delete them:

- **New external script** starts from a template that shows how a filter receives the script being filtered (`ioBean.getInput('script')`) and changes its steps.
- The **Name** must end in `.js` for the controller to run it as JavaScript. See [Scripting languages](scripting.md) for the languages a controller can run and the objects a script can use.
- **Test it** runs the script, as edited, against one of your scripts and shows the changes it would make to the steps and its output. Nothing is saved.

You need the *Create Script* right to create external scripts. You can also write and test them on your own machine with the Script Filter Editor in the [supplemental tools](../supplemental-tools.md#script-filter-editor).

## Creating a filter group

1. Click **New group**.
2. Enter a **Name**, and optionally choose a **Product**.
3. Under **Filters**, tick the filters that belong to the group. Search narrows the list.
4. Click **Create**.

## Permissions

Creating filters and groups needs the *Create Filter* right. Changing or deleting someone else's filter or group needs *Edit Filter* or *Delete Filter*; you can always change and delete your own. Applying filters to a script needs the right to edit that script. See [Roles and permissions](../roles.md).
