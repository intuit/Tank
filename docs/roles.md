# Roles and Permissions

Tank controls who can do what with **groups**. Each user belongs to one or more groups, and each group grants a set of **rights**. Administrators assign groups on the [Admin › Users](administration.md#users) page.

Three rules apply on top of the groups:

- **Everyone who is signed in can view** every project, script, filter, filter group and data file, and the Agent Tracker.
- **Owners can always change and delete what they own**, and can queue, run and control the jobs of projects they own, whatever their groups.
- **Members of the `admin` group can do everything**, including the [administration](administration.md) pages.

The owner of an item is the user who created it. The owner or an administrator can give a project, filter or filter group to another user by changing its **Owner**.

## Groups

These are the groups in Tank's default configuration. New users, whether created by an administrator or signed in for the first time through SSO, get the default groups, **user** and **guest**.

| Group | Role |
| :---- | :--- |
| **guest** | Can view everything, but has no rights to create or change anything. *(default)* |
| **user** | Can create projects, scripts, filters and filter groups, and upload data files. Can only change and delete what they own. *(default)* |
| **project-manager** | Maintains projects: can create, change and delete anyone's projects, control any project's jobs, and upload and delete data files. Can't create scripts or filters. |
| **script-manager** | Maintains scripts and filters: can create, change and delete anyone's scripts, filters and filter groups, and upload and delete data files. Can't create projects or control jobs. |
| **job-manager** | Controls jobs: can run, pause, stop, kill and delete any project's jobs. Can't create or change projects. |
| **admin** | Can do everything. |

## Rights

The rights, and the groups that hold them by default:

| Right | Lets you | Default groups |
| :---- | :------- | :------------- |
| Create Project | create and copy projects | user, project-manager |
| Edit Project | change anyone's project | project-manager |
| Delete Project | delete anyone's project | project-manager |
| Create Script | create, copy and import scripts; create external filter scripts | user, script-manager |
| Edit Script | change anyone's script, including applying filters to it | script-manager |
| Delete Script | delete anyone's script | script-manager |
| Create Filter | create and copy filters and filter groups | user, script-manager |
| Edit Filter | change anyone's filter or filter group | script-manager |
| Delete Filter | delete anyone's filter or filter group | script-manager |
| Create Data File | upload data files | user, script-manager, project-manager |
| Edit Data File | replace the contents of anyone's data file | none (owners and admins only) |
| Delete Data File | delete anyone's data file | script-manager, project-manager |
| Control Job | queue, run and control any project's jobs, and delete jobs that haven't started | job-manager, project-manager |

## Permissions matrix

What each default group can do with items it doesn't own. You can always change and delete items you own, and admins can do everything.

| | guest | user | project-manager | script-manager | job-manager |
| :- | :-: | :-: | :-: | :-: | :-: |
| **Projects**: view | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| **Projects**: create | | :white_check_mark: | :white_check_mark: | | |
| **Projects**: change, delete | | | :white_check_mark: | | |
| **Jobs**: queue, run, control | | | :white_check_mark: | | :white_check_mark: |
| **Scripts**: view | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| **Scripts**: create | | :white_check_mark: | | :white_check_mark: | |
| **Scripts**: change, delete | | | | :white_check_mark: | |
| **Filters and groups**: view | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| **Filters and groups**: create | | :white_check_mark: | | :white_check_mark: | |
| **Filters and groups**: change, delete | | | | :white_check_mark: | |
| **Data files**: view | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| **Data files**: upload | | :white_check_mark: | :white_check_mark: | :white_check_mark: | |
| **Data files**: replace contents | | | | | |
| **Data files**: delete | | | :white_check_mark: | :white_check_mark: | |

## Changing the groups and rights

The groups, the default groups and the rights each group holds are set in the `security` section of the controller's `settings.xml`:

```xml
<security>
    <groups>
        <group>admin</group>
        <group isDefault="true">user</group>
        <group>script-manager</group>
        <group>project-manager</group>
        <group>job-manager</group>
        <group isDefault="true">guest</group>
    </groups>
    <restrictions>
        <restriction operation="CREATE_PROJECT">
            <groups>
                <group>user</group>
                <group>project-manager</group>
            </groups>
        </restriction>
        <!-- one restriction per right: CREATE_, EDIT_ and DELETE_ PROJECT, SCRIPT,
             FILTER and DATAFILE, and CONTROL_JOB -->
    </restrictions>
</security>
```

`isDefault="true"` marks the groups new users get. A right with no `restriction` is held by no group, so only owners and admins have it.

!!! warning "REST security"
    These rules apply to the web UI and to REST calls when `rest-security-enabled` is `true` in `settings.xml`. When it is `false`, REST calls without credentials are allowed, as they were before REST authorization existed. Enable it on any shared controller.
