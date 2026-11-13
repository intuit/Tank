# Data Files

Data files feed your scripts with test data, such as logins, account numbers or search terms, so each virtual user works with different values. A [project](projects.md#data-files) lists the data files its scripts read, and when a job runs, each file's lines are divided evenly among the job's agents.

## The Data files page

Open **Data files** in the menu bar. Like the other lists, it is paged, searchable, can be narrowed to one owner, and remembers your columns.

Each row can be:

- **previewed**, 50 lines a page;
- **downloaded**;
- given **new contents** (**Replace contents**): upload a `.csv`, `.txt` or `.xml` file, or a gzipped `.gz` one. The data file keeps its name, so projects that use it get the new contents in their next job;
- **deleted**. Tick several rows to delete them together.

## Uploading data files

1. Click **Upload** (or **Upload data files** on a project's *Data files* tab, which also adds the files to the project).
2. Choose `.csv`, `.txt` or `.xml` files, or `.zip` files. Every `.csv`, `.txt` and `.xml` file in a zip, in any folder, becomes a data file named after it. You can upload up to 50 files at once.
3. Click **Upload**.

A name that is already in use adds another file with that name; it doesn't replace the existing one. Use **Replace contents** to update a file.

You need the *Create Data File* right to upload. Replacing or deleting someone else's file needs *Edit Data File* or *Delete Data File*; you can always replace and delete your own. See [Roles and permissions](../roles.md).

## Reading data in a script

Scripts read data files with the `ioFunctions` [functions](io-functions.md):

- `#{ioFunctions.getCSVData(column)}` reads the **default** data file: the job's only data file, when it has exactly one.
- `#{ioFunctions.getCSVData('fileName', column)}` reads a named file, for when a project has several, such as one for personal details and one for login credentials.

Columns are numbered from 0. Each virtual user works through its own lines, and no two users on an agent get the same line. [IO Functions](io-functions.md#how-csv-lines-are-read) explains how lines are handed out and what happens when a file runs out.

!!! tip
    Add every data file your scripts read to the project. A job only sends its agents the project's data files.

## How lines are divided among agents

When a job starts, the controller divides each data file's lines evenly among the job's agents, so each agent reads a different part of the file. With 6,000 lines and two agents, each agent gets 3,000 lines. When the lines don't divide evenly, the few left over aren't used.
