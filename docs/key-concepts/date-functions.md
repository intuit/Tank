# Date Functions

| Method Definition | Description                          |  Parameters | Example    |
| :---------------------------------------| :----------------------------------: | :----:| :--------:|
| `addDays(int days, String format)`| Adds days to the current date. | 1. `days`: Integer The number of days. Pass in a negative value to subtract days. 2. `format`: String The date format string to use. If empty, will use default for locale.      | `#{dateFunctions.addDays(5,'yyyy-MM-dd')}` produces `2011-11-20` on `November 15, 2011`          |
| `currentDate(String format, String timeZone)`| Current Date. Get the current date. | 1. `format`: String The date format string to use. If empty, will use default for locale. 2. `timeZone`: String (optional) The timezone to use. ()e.g. 'PST', 'America/Los_Angeles', or 'GMT'). If empty, will use default for locale.      | `#{dateFunctions.currentDate('yyyy-MM-dd', 'PST')}` produces `2011-11-15` on `November 15, 2014`         |
| `currentTimeMilis()`| Gets the current Time in miliseconds since January 1, 1970. (Unix epoch time) | - | `#{dateFunctions.currentTimeMilis()}` produces a long number like `1357842009812`          |