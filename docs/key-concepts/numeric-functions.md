# Numeric Functions

| Method Definition | Description                          |  Parameters | Example    |
| :---------------------------------------| :----------------------------------: | :----:| :--------:|
| `add(double... values)`| Adds a list of values | `values`: Doubles (required) Variable number of number to add together.  |`#{numericFunctions.add(3,2,6)}` results in `3 + 2 + 6 = 11`|
| `subtract(double... values)`| Subtracts a list of values from the first value.| 1. `values`: Doubles (required) Variable number of number to subtract.    | `#{numericFunctions.subtract(10,2,3)}` results in `10 - 2 - 3 = 5`|
| `mod(int value, int modulo)`| Performs a modulo operation, or the whole remainder of a division operation.| 1. `value`: Integer (required) The number ot modulo. 2. `modulo`: Integer (required).   | `#{numericFunctions.mod(5,4)}` results in `5 % 4 = 1`|
| `randomPositiveWhole(int length)`| Gets a random positive integer value.| `length`: Integer (required) The number of digits in the whole amount. |` #{numericFunctions.randomPositiveWhole(3)}` produces a random value between 1`00` and `999`|
| `randomNegativeWhole(int length)`| Gets a random negative integer value.| `length`: Integer (required) The number of digits in the whole amount. | `#{numericFunctions.randomNegativeWhole(3)}` produces a random value between `-100` and `-999`|
| `randomPositiveFloat(int length, int decimalPlaces)`| Gets a random positive float value.| 1.`length`: Integer (required) The number of digits in the whole amount. 2. `decimalPlaces`: Integer (required) The number of digits after the decimal.|`#{numericFunctionS.randompPositiveFloat(3,2)}` produces a random value between `100.00` and `999.99`|
| `randomNegativeFloat(int length, int decimalPlaces)`| Gets a random negative float value.| 1.`length`: Integer (required) The number of digits in the whole amount. 2. `decimalPlaces`: Integer (required) The number of digits after the decimal. | `#{numericFunctions.randomNegativeFloat(3,2)}` produces a random value between `-100.00` and `-999.00`|
| `random(int min, int max)`| Gets a random negative float value.| 1.`min`: Integer (optional) The minimum value to return. default 0. 2. `max`: Integer (required) maximum value to return. | `#{numericFunctions.random(4,10)}` produces a random value between `4` and `10`|

