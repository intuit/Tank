# Monetary Functions

| Method Definition | Description                          |  Parameters | Example    |
| :---------------------------------------| :----------------------------------: | :----:| :--------:|
| `randomPositive(int length)`| Gets a random positive money amount. | 1. `length`: Integer (required) The number of digits in the whole amount.     | `#{monetaryFunctions.randomPositive(3)}` produces a random monetary amount between `100.00` and `999.99`|
| `randomNegative(int length)`| Gets a random negative money amount.| 1. `length`: Integer (required) The number of digits in the whole amount.     | `#{monetaryFunctions.randomNegative(3)}` produces a random monetary amount between `-100.00` and `-999.99`|
