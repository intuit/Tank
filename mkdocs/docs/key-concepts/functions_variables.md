# Functions and Variables

Functions can be used to manipulate data during test execution. They can be used anywhere in place of a Value.  

Functions and Variables both use the same format. They start with either a `#` or `$` and then are enclosed in parentheses. `e.g. #{functionType.function(param, param2)}` or `${functionType.function(param, param2)}`. Variables have no parentheses. `e.g. #{varName}`

Functions or variables can be nested in other functions.   
`e.g. #{stringFunctions.toBase64(ioFunctions.getFileData(fileName)}`   
In this example the variable is `fileName` which is passed to the `getFileDataFunction` which is passed to the `toBase64` function.

??? info "Example: Using Functions"
    * Declaring the function `#{stringFunctions.concat('Intuit ', 'Tank ',' Rocks')}` would yield `Intuit Tank Rocks`.
    * Declaring the function `#{numericFunctions.mod(authId, 10)}` with the variable `${authId}` defined as `51 `would result in `51 % 10 = 1`.


## Types of Functions
There are 6 built in function types.  

- `ioFunctions`
- `stringFunctions`
- `dateFunctions`
- `monetaryFunctions`
- `numericFunctions`
- `taxFunctions`


??? info "Tip: Types of Functions and their usage"
    Check out the sub-sction `Types of Functions` on the left navigation bar for Information on each Function Type and their usage.