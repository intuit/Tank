# Tax Functions

`taxFunctions` generate test data for tax applications.

| Function | Description | Example |
| :------- | :---------- | :------ |
| `getSsn(startSsn)` | The next valid Social Security Number, formatted `NNN-NN-NNNN`. The first call on an agent starts at `startSsn`; every later call on that agent, from any virtual user, returns the next valid number after the previous one (later `startSsn` values are ignored). Numbers outside the issued SSN ranges are skipped. | `#{taxFunctions.getSsn(1010001)}` returns `001-01-0001`, then `001-01-0002`, ... |

!!! warning "Known limitations"
    - The sequence isn't split between agents, so two agents started with the same `startSsn` return the same numbers. For numbers that are unique across agents, use the legacy `#function.generic.getssn.<startSsn>`, which gives each agent its own share of the range.
    - A nine-digit `startSsn` (100000000 or more) gives a malformed result: `getSsn(562000000)` returns `056-20-0000`. Start below 100000000, or use `#function.generic.getssn`.

See [Legacy `#function` syntax](functions_variables.md#legacy-function-syntax) for `#function.generic.getssn` and `#function.tax.getssn`.
