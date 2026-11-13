# Date Functions

`dateFunctions` return the current date and time as strings. A `format` is a Java date pattern such as `yyyy-MM-dd` or `MM/dd/yyyy HH:mm:ss` ([`SimpleDateFormat`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/SimpleDateFormat.html) syntax). An empty or invalid `format` falls back to the agent locale's default date format, unless the row says otherwise.

| Function | Description | Example |
| :------- | :---------- | :------ |
| `currentDate(format)` | Today's date in the agent's time zone. | `#{dateFunctions.currentDate('yyyy-MM-dd')}` returns `2011-11-15` on November 15, 2011 |
| `currentDate(format, timeZone)` | Today's date in `timeZone`, such as `'PST'`, `'America/Los_Angeles'` or `'GMT'`. A blank `timeZone` uses the agent's. | `#{dateFunctions.currentDate('yyyy-MM-dd', 'America/Los_Angeles')}` |
| `currentDateTime(format)` | The current date and time in the agent's time zone. An empty `format` gives `yyyy-MM-dd HH:mm:ss`. | `#{dateFunctions.currentDateTime('yyyy-MM-dd HH:mm:ss')}` returns e.g. `2025-08-18 22:11:28` |
| `currentDateTime(format, timeZone)` | The current date and time in `timeZone`. | `#{dateFunctions.currentDateTime('HH:mm', 'GMT')}` |
| `currentUTC()` | The current UTC time in ISO 8601 format. | `#{dateFunctions.currentUTC()}` returns e.g. `2025-08-18T22:11:28.600Z` |
| `currentUTC(format)` | The current UTC time in `format`, or one of the named formats below. An empty or invalid `format` gives ISO 8601. | `#{dateFunctions.currentUTC('ISO_UTC_NO_MS')}` returns e.g. `2025-08-18T22:11:28Z` |
| `addDays(days, format)` | The date `days` days from today in the agent's time zone. Use a negative `days` to go back. | `#{dateFunctions.addDays(5, 'yyyy-MM-dd')}` returns `2011-11-20` on November 15, 2011 |
| `currentTimeMilis()` | Milliseconds since the Unix epoch (January 1, 1970 UTC). Note the spelling: one `l`. | `#{dateFunctions.currentTimeMilis()}` returns e.g. `1357842009812` |

## Named UTC formats

`currentUTC` (and the legacy `#function.date.currentUTC` and `#function.date.addDaysUTC`) accept these names in place of a pattern:

| Name | Pattern | Example |
| :--- | :------ | :------ |
| `ISO_UTC` | `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` | `2025-08-18T22:11:28.600Z` |
| `ISO_UTC_NO_MS` | `yyyy-MM-dd'T'HH:mm:ss'Z'` | `2025-08-18T22:11:28Z` |
| `ISO_UTC_OFFSET` | `yyyy-MM-dd'T'HH:mm:ss.SSSXXX` | `2025-08-18T22:11:28.600Z` |
| `ISO_UTC_DATE` | `yyyy-MM-dd` | `2025-08-18` |
| `ISO_UTC_TIME` | `HH:mm:ss.SSS'Z'` | `22:11:28.600Z` |
| `RFC_1123` | `EEE, dd MMM yyyy HH:mm:ss zzz` | `Mon, 18 Aug 2025 22:11:28 Z` |

!!! note "No `addDaysUTC` in `#{...}`"
    There is no `dateFunctions.addDaysUTC`; `#{dateFunctions.addDaysUTC(...)}` returns an empty string. To add days in UTC, use the legacy form `#function.date.addDaysUTC.<days>.<format>`, for example `#function.date.addDaysUTC.1.ISO_UTC`. See [Legacy `#function` syntax](functions_variables.md#legacy-function-syntax).
