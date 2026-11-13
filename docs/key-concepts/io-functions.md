# IO Functions

`ioFunctions` read the [data files](datafiles.md) attached to a project. Files are read from the agent's data file directory.

| Function | Description | Example |
| :------- | :---------- | :------ |
| `getCSVData()` | Same as `getCSVData(0, false)`: column 0 of the default data file, `csv-data.txt`. | `#{ioFunctions.getCSVData()}` |
| `getCSVData(columnIndex)` | Same as `getCSVData(columnIndex, false)`. | `#{ioFunctions.getCSVData(2)}` |
| `getCSVData(columnIndex, loop)` | Column `columnIndex` (zero-based) of the current line of `csv-data.txt`. Set `loop` to `true` to start again at the first line when the file runs out. | `#{ioFunctions.getCSVData(1, true)}` |
| `getCSVData(fileName)` | Same as `getCSVData(fileName, 0, false)`. | `#{ioFunctions.getCSVData('users.csv')}` |
| `getCSVData(fileName, columnIndex)` | Same as `getCSVData(fileName, columnIndex, false)`. | `#{ioFunctions.getCSVData('users.csv', 1)}` returns the second column |
| `getCSVData(fileName, columnIndex, loop)` | Column `columnIndex` (zero-based) of the current line of `fileName`. `loop`: `true` to go back to the first line after the last one is read. | `#{ioFunctions.getCSVData('users.csv', 1, true)}` |
| `getFileData(fileName)` | The whole file as a string. The file is read once per agent and cached. An empty string if the file doesn't exist. | `#{ioFunctions.getFileData('body.json')}` |
| `getFileBytes(fileName)` | The whole file as a byte array (`byte[]`). It isn't a string, so use it only as the argument of a function that takes bytes, such as `stringFunctions.byteArrayToBase64`. | `#{stringFunctions.byteArrayToBase64(ioFunctions.getFileBytes('logo.png'))}` |

## How CSV lines are read

- Each virtual user has its own current line in each file. Lines are handed out in order, so no two users on an agent get the same line.
- Read each column of a line once. When a user asks for a column it has already read on its current line (or one past the end of the line), it moves on to the next line. Reading columns 0, 1 and 2 in turn gives the three values of one line; reading column 0 twice gives the first value of two lines.
- Lines are split on every comma. Quoted values with commas in them aren't supported.
- When the file runs out and `loop` is `false`, the virtual user is stopped (the agent logs `CSV file (...) has no more data.`).

!!! tip "Variables as arguments"
    Arguments can be variables or other functions: `#{ioFunctions.getCSVData(dataFile, 0, true)}` reads the file named by the variable `dataFile`.

The legacy `#function.generic.getcsv` and `#function.generic.getfiledata` forms are listed under [Legacy `#function` syntax](functions_variables.md#legacy-function-syntax).
