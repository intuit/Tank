# Monetary Functions

`monetaryFunctions` generate random money amounts with two decimal places. `length` is the number of digits before the decimal point.

| Function | Description | Example |
| :------- | :---------- | :------ |
| `randomPositive(length)` | A random positive amount. | `#{monetaryFunctions.randomPositive(3)}` returns e.g. `225.08` |
| `randomNegative(length)` | A random negative amount. | `#{monetaryFunctions.randomNegative(3)}` returns e.g. `-225.08` |

These are shorthand for `numericFunctions.randomPositiveFloat(length, 2)` and `numericFunctions.randomNegativeFloat(length, 2)`, and produce digits the same way (see [Numeric Functions](numeric-functions.md#random-numbers)).
