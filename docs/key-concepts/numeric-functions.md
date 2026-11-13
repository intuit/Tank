# Numeric Functions

`numericFunctions` do arithmetic and generate random numbers. Arguments can be numbers, numeric strings or variables holding numbers, and every result is a string.

## Arithmetic

| Function | Description | Example |
| :------- | :---------- | :------ |
| `add(values...)` | Adds any number of values. The result is a decimal. | `#{numericFunctions.add(3, 2, 6)}` returns `11.0` |
| `subtract(values...)` | Subtracts the second and later values from the first. The result is a decimal. | `#{numericFunctions.subtract(10, 2, 3)}` returns `5.0` |
| `mod(value, modulo)` | The remainder of whole-number division `value % modulo`. | `#{numericFunctions.mod(authId, 10)}` returns `1` when `authId` is `51` |
| `toInt(value)` | Drops the fractional part of a number. Useful to turn the result of `add` or `subtract` into a whole number. | `#{numericFunctions.toInt(numericFunctions.add(1, 2))}` returns `3` |

## Random numbers

| Function | Description | Example |
| :------- | :---------- | :------ |
| `random(max)` | A random whole number from 0 (inclusive) to `max` (exclusive). | `#{numericFunctions.random(10)}` returns `0` to `9` |
| `random(min, max)` | A random whole number from `min` (inclusive) to `max` (exclusive). Returns `0` if `max` isn't greater than `min`. | `#{numericFunctions.random(4, 10)}` returns `4` to `9` |
| `randomPositiveWhole(length)` | A random whole number with `length` digits. An empty string if `length` is `0`. | `#{numericFunctions.randomPositiveWhole(3)}` returns e.g. `427` |
| `randomNegativeWhole(length)` | The negative of `randomPositiveWhole(length)`. | `#{numericFunctions.randomNegativeWhole(3)}` returns e.g. `-427` |
| `randomPositiveFloat(length, decimalPlaces)` | A random number with `length` digits before the decimal point and `decimalPlaces` after it. | `#{numericFunctions.randomPositiveFloat(3, 2)}` returns e.g. `427.15` |
| `randomNegativeFloat(length, decimalPlaces)` | The negative of `randomPositiveFloat(length, decimalPlaces)`. | `#{numericFunctions.randomNegativeFloat(3, 2)}` returns e.g. `-427.15` |

!!! note "Digits used by the `randomPositive*` and `randomNegative*` functions"
    These functions build the number one digit at a time: the first digit is `1`–`8` and each later digit is `0`–`8`. The digit `9` never appears, so `randomPositiveWhole(3)` returns a value from `100` to `888`. Use `random(min, max)` when you need every value in a range.
