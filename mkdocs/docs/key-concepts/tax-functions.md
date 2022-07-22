# Tax Functions

| Method Definition | Description                          |  Parameters | Example    |
| :---------------------------------------| :----------------------------------: | :----:| :--------:|
| `getSsn(long startSSN)`| Gets a valid Social Security Number starting at at specific number. Each user will get a unique ssn. range is divided evenly across all agents. | `startSSN`: Integer The starting number to use as a ssn. |`#{taxFunctions.getSsn(562000000)}` returns the next ssn requested after the given number.|