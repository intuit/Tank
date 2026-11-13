# Functions and Variables

Variables and functions make a script dynamic: they put different data into each request, for each virtual user, while the test runs. They're evaluated on the agent.

## Variables

A variable is a named string. You can set one:

- **In the project**, as a global variable on the project's **Variables** tab (see [Projects](projects.md)). Unless you allow scripts to override project variables, a script step can't change them.
- **With a Variable step** in a script (see [Scripts](scripts.md#variables)). The value can be a literal, an expression or a function.
- **With an assignment** on a request, which stores part of the response (body, header or cookie) in a variable.
- **From a Logic step**, with `setVariable(name, value)` (see [Scripting Languages](scripting.md)).

Each virtual user has its own copy of every variable. Tank also sets these:

| Variable | Description |
| :------- | :---------- |
| `THREAD_ID` | The number of the virtual user on its agent. It can't be overridden. |
| `TANK_HTTP_PROXY` | Not set by Tank. If you set it to `host:port`, the agent sends requests through that proxy, for example `myProxyserver:80`. |

## Using variables and functions

There are two ways to use a variable, and two to call a function.

| Syntax | Where it works | Example |
| :----- | :------------- | :------ |
| `#{expression}` | Anywhere in a value, any number of times. Use this. | `#{rootContext}/ajax/logEmail` |
| `@name` | Only when it is the whole value. Replaced by the value of variable `name`. | `@webserver` in a request's Host |
| `#function.category.name.arg1.arg2` | Only when it is the whole value. The legacy function syntax, described [below](#legacy-function-syntax). | `#function.date.currentDate.yyyy-MM-dd` |

`#{...}` expressions are evaluated in request hosts, paths, protocols and ports, query strings, POST data and request bodies, headers, cookies, validations, assignments, authentication fields, think and sleep times, and Variable steps.

!!! warning "`${...}` isn't evaluated"
    Older guides show `${name}` as an alternative to `#{name}`. Tank no longer evaluates it: `${name}` is sent as the literal text `${name}`. Use `#{name}`.

### Expressions

The text between `#{` and `}` is a [JEXL](https://commons.apache.org/proper/commons-jexl/reference/syntax.html) expression. Inside it:

- A bare name is a variable: `#{userName}`. Use letters, digits and underscores in names you refer to this way; `#{user-name}` would mean `user` minus `name`.
- Strings are quoted with `'` or `"`: `#{stringFunctions.concat('Intuit ', 'Tank')}`.
- Functions are called as `category.function(arguments)`, and arguments can be variables or other function calls:

    ```text
    #{stringFunctions.toBase64(ioFunctions.getFileData(fileName))}
    ```

    Here `fileName` is a variable whose value is passed to `getFileData`, whose result is passed to `toBase64`.

- `+` joins strings and `? :` chooses between values: `#{env == 'prod' ? 'api.example.com' : 'qa.example.com'}`.

For example, with a variable `authId` set to `51`, `#{numericFunctions.mod(authId, 10)}` returns `1`, and `#{stringFunctions.concat('Intuit ', 'Tank ', 'Rocks')}` returns `Intuit Tank Rocks`.

Keep these rules in mind:

- **An expression must produce a string.** All the built-in functions return strings, apart from `ioFunctions.getFileBytes`. Arithmetic such as `#{count + 1}` produces a number and fails the step; use `#{numericFunctions.add(count, 1)}`.
- **Errors give an empty string.** An unknown variable, a misspelled function or wrong arguments evaluate to `""` rather than stopping the test. If a value comes out empty, check the spelling against the function pages.
- **An expression ends at the first `}`**, so it can't contain a literal `}`.
- **When an expression is evaluated matters.** A Variable step evaluates its value once, when the step runs: a variable set to `#{stringFunctions.getUUID()}` keeps the same UUID until it is set again. An expression written straight into a request field is evaluated every time the request runs.
- Only Tank's functions can be called. Java methods on values, such as `#{name.toUpperCase()}`, aren't allowed and return an empty string.

## Built-in functions

| Category | What it does |
| :------- | :----------- |
| [`ioFunctions`](io-functions.md) | Read values from CSV data files, and read whole files. |
| [`stringFunctions`](string-functions.md) | Join, cut, encode and decode strings; random strings, UUIDs and user IDs. |
| [`dateFunctions`](date-functions.md) | The current date and time, in any format and time zone. |
| [`monetaryFunctions`](monetary-functions.md) | Random money amounts. |
| [`numericFunctions`](numeric-functions.md) | Arithmetic and random numbers. |
| [`taxFunctions`](tax-functions.md) | Social Security Numbers. |

## Legacy `#function` syntax

Before `#{...}` expressions, Tank called functions with a dotted syntax: `#function.<category>.<name>.<arg1>.<arg2>...`. It still works, and a few functions are only available this way, but prefer `#{...}` for new scripts. The rules:

- The value must start with `#function`, and the function is the whole value; it can't be combined with other text.
- Category and function names aren't case sensitive.
- Arguments are separated by `.`. To put a dot inside an argument, write `-dot-`: `#function.date.currentDateTime.HH:mm:ss-dot-SSS`.
- An argument that starts with `@` is replaced by that variable's value: `#function.string.concat.user-.@THREAD_ID`. This doesn't work for the `numeric` functions or `monetary.randomNegative`, whose arguments must be literal numbers.
- A function that fails, or a value that isn't a valid function, gives an empty value.

| Function | Description |
| :------- | :---------- |
| `#function.date.currentDate.<format>` | Today's date in the agent's time zone. |
| `#function.date.currentDateTime.<format>` | The current date and time. Default format `yyyy-MM-dd HH:mm:ss`. |
| `#function.date.currentUTC.<format>` | The current UTC time. Accepts the [named UTC formats](date-functions.md#named-utc-formats); default ISO 8601. |
| `#function.date.addDays.<days>.<format>` | The date `days` days from today (negative to go back). |
| `#function.date.addDaysUTC.<days>.<format>` | The UTC date and time `days` days from now. Accepts the named UTC formats; default ISO 8601. Example: `#function.date.addDaysUTC.1.ISO_UTC`. |
| `#function.monetary.randomPositive.<length>` | A random positive amount with `length` whole digits and two decimals. |
| `#function.monetary.randomNegative.<length>` | A random negative amount. |
| `#function.numeric.add.<v1>.<v2>...` | The sum of the values, as a decimal (`#function.numeric.add.1.2.3` gives `6.0`). Write decimals with `-dot-`. |
| `#function.numeric.subtract.<v1>.<v2>...` | The first value minus the others, as a decimal. |
| `#function.numeric.mod.<value>.<modulo>` | `value % modulo`. |
| `#function.numeric.randomPositiveWhole.<length>` | A random whole number with `length` digits. |
| `#function.numeric.randomNegativeWhole.<length>` | Its negative. |
| `#function.numeric.randomPositiveFloat.<length>.<decimals>` | A random number with `length` whole digits and `decimals` decimal places. |
| `#function.numeric.randomNegativeFloat.<length>.<decimals>` | Its negative. |
| `#function.string.concat.<v1>.<v2>...` | The arguments joined together. |
| `#function.string.substring.<subject>.<start>[.<stop>]` | Part of `subject` from `start` (inclusive) to `stop` (exclusive, optional). |
| `#function.string.substring.<startMarker>.<endMarker>` | In a body assignment or body validation only: the text of the response body between the first `startMarker` and the next `endMarker` (to the end of the body if `endMarker` isn't found). Used when the second argument isn't a number. Example: `#function.string.substring.token=.&` |
| `#function.string.lastSubstring.<startMarker>.<endMarker>` | Like the response-body `substring`, but starts at the last `startMarker`. Useful for the final event of a streamed response. |
| `#function.string.userid.<prefixLength>.<format>` | A random letters-and-digits prefix followed by today's date, like `stringFunctions.userIdDate`. |
| `#function.string.randomAlphaLower.<length>` | Random lower-case letters. |
| `#function.string.randomAlphaUpper.<length>` | Random upper-case letters. |
| `#function.string.randomAlphaMixed.<length>` | Random letters. |
| `#function.string.randomNumeric.<length>` | Random digits. |
| `#function.string.randomSpecial.<length>` | Random special characters. |
| `#function.string.randomAlphaMixedNumeric.<length>` | Random letters and digits. |
| `#function.string.randomAlphaMixedSpecial.<length>` | Random letters and special characters. |
| `#function.string.randomAlphaMixedNumericSpecial.<length>` | Random letters, digits and special characters. |
| `#function.string.useridFromRange.<min>.<max>[.<patterns>]` | A unique number from `min` (inclusive) to `max` (exclusive), split between agents like `stringFunctions.userIdFromRange`. Numbers matching any of `patterns` are skipped. |
| `#function.string.useridFromRangeExclude.<min>.<max>.<patterns>` | The same as `useridFromRange` with patterns. |
| `#function.string.useridFromRangeInclude.<min>.<max>.<patterns>` | As `useridFromRange`, but only numbers that match one of `patterns`. |
| `#function.string.useridFromRangeWithMod.<min>.<max>.<mod>` | As `useridFromRange`, but only multiples of `mod`. |
| `#function.string.useridFromRangeWithModInclude.<min>.<max>.<mod>` | The same as `useridFromRangeWithMod`. |
| `#function.string.useridFromRangeWithModExclude.<min>.<max>.<mod>` | As `useridFromRange`, but only numbers that aren't multiples of `mod`. |
| `#function.generic.getcsv.<column>` | Column `column` (zero-based) of the next line of `csv-data.txt`, read the same way as [`ioFunctions.getCSVData`](io-functions.md). Empty when the file runs out. |
| `#function.generic.getfiledata.<fileName>.<column>` | Column `column` of the next line of `fileName`. Dots in the file name can be written as-is. |
| `#function.generic.getssn[.<startSsn>]` | The next valid Social Security Number, unique across all agents: the range from `startSsn` (default 100000000) to 899999999 is split between the agents. |
| `#function.tax.getssn` | The next valid Social Security Number after the value of the variable `SSN_START`, like `taxFunctions.getSsn`. Empty if `SSN_START` isn't set. |
| `#function.datatype.<name>` | A boundary value for testing input validation. See the table below. |

`patterns` for the `useridFromRange` functions are comma-separated Java regular expressions, each matched against the whole number: `#function.string.useridFromRangeInclude.1000.2000.1[0-4]\d\d` gives numbers from 1000 to 1499. Write any `.` in a pattern as `-dot-`.

The `datatype` names are `BYTE_Max`, `BYTE_Min`, `BYTE_Max_Plus`, `BYTE_Min_Minus`, `SHORT_Max`, `SHORT_Min`, `SHORT_Max_Plus`, `SHORT_Min_Minus`, `INT_Max`, `INT_Min`, `INT_Max_Plus`, `INT_Min_Minus`, `LONG_Max`, `LONG_Min`, `LONG_Max_Plus` and `LONG_Min_Minus`: the largest or smallest value of the Java type, or one beyond it. For example, `#function.datatype.SHORT_Max_Plus` gives `32768`.

!!! warning
    `INT_Max_Plus` and `INT_Min_Minus` overflow: they give `-2147483648` and `2147483647` rather than `2147483648` and `-2147483649`.
