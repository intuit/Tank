# String Functions

`stringFunctions` build, cut, encode and generate strings.

## Building and cutting strings

| Function | Description | Example |
| :------- | :---------- | :------ |
| `concat(values...)` | Joins any number of strings. | `#{stringFunctions.concat('Intuit ', 'Tank', ' Rocks')}` returns `Intuit Tank Rocks` |
| `substring(subject, start)` | `subject` from index `start` (zero-based) to the end. | `#{stringFunctions.substring('hello world', 6)}` returns `world` |
| `substring(subject, start, stop)` | `subject` from `start` (inclusive) to `stop` (exclusive). A `stop` of `-1` or past the end means "to the end". | `#{stringFunctions.substring('hello world', 0, 5)}` returns `hello` |
| `substringBetween(subject, open, close)` | The first string found between `open` and `close`. | `#{stringFunctions.substringBetween('yabcz', 'y', 'z')}` returns `abc` |
| `substringBetween(subject, open, close, index)` | The `index`-th (zero-based) string found between `open` and `close`. If `open` is `null`, returns everything before the first `close`; if `close` is `null`, everything after the last `open`. Empty if there is no match. | `#{stringFunctions.substringBetween('yabcz ydefz', 'y', 'z', 1)}` returns `def` |
| `replaceBetween(subject, open, close, replace, encodeData)` | Replaces the text between the first `open` and the next `close` with `replace`, keeping `open` and `close`. A `null` `open` or `close` means the start or end of `subject`. With `encodeData` `true`, `subject` is Base64-decoded first and the result is Base64-encoded again. | `#{stringFunctions.replaceBetween('a[b]c', '[', ']', 'X', false)}` returns `a[X]c` |

## Encoding

| Function | Description | Example |
| :------- | :---------- | :------ |
| `toBase64(toEncode)` | Base64-encodes a string. | `#{stringFunctions.toBase64('ab')}` returns `YWI=` |
| `fromBase64(toDecode)` | Decodes a Base64 string (as UTF-8). | `#{stringFunctions.fromBase64('YWI=')}` returns `ab` |
| `byteArrayToBase64(bytes)` | Base64-encodes a byte array, such as the result of `ioFunctions.getFileBytes`. | `#{stringFunctions.byteArrayToBase64(ioFunctions.getFileBytes('logo.png'))}` |
| `urlEncode(toEncode)` | URL-encodes a string (UTF-8, `application/x-www-form-urlencoded`). | `#{stringFunctions.urlEncode('a b&c')}` returns `a+b%26c` |
| `urlDecode(toDecode)` | URL-decodes a string (UTF-8). | `#{stringFunctions.urlDecode('a+b%26c')}` returns `a b&c` |

## Random strings and IDs

Each `length` is the number of characters to generate.

| Function | Characters used | Example |
| :------- | :-------------- | :------ |
| `randomAlphaLower(length)` | `a`–`z` | `#{stringFunctions.randomAlphaLower(8)}` |
| `randomAlphaUpper(length)` | `A`–`Z` | `#{stringFunctions.randomAlphaUpper(8)}` |
| `randomAlphaMixed(length)` | `a`–`z`, `A`–`Z` | `#{stringFunctions.randomAlphaMixed(8)}` |
| `randomNumeric(length)` | `0`–`9` (may start with `0`) | `#{stringFunctions.randomNumeric(4)}` |
| `randomSpecial(length)` | ``!@#$%^*{[}]:;,.?~` `` | `#{stringFunctions.randomSpecial(4)}` |
| `randomAlphaMixedNumeric(length)` | letters and digits | `#{stringFunctions.randomAlphaMixedNumeric(8)}` |
| `randomAlphaMixedSpecial(length)` | letters and special characters | `#{stringFunctions.randomAlphaMixedSpecial(8)}` |
| `randomAlphaMixedNumericSpecial(length)` | letters, digits and special characters | `#{stringFunctions.randomAlphaMixedNumericSpecial(8)}` |
| `getUUID()` | A random UUID. | `#{stringFunctions.getUUID()}` returns e.g. `6a244e4f-d4ea-4a7b-8882-73ca5c729527` |

| Function | Description | Example |
| :------- | :---------- | :------ |
| `userIdDate(prefixLength, format)` | A random prefix of `prefixLength` letters and digits followed by today's date in `format` (a Java `SimpleDateFormat` pattern). | `#{stringFunctions.userIdDate(4, 'yyyy-MM-dd')}` returns e.g. `GdGE2011-11-15` on November 15, 2011 |
| `userIdFromDate(prefixLength, format)` | Same as `userIdDate`. | `#{stringFunctions.userIdFromDate(4, 'yyyy-MM-dd')}` |
| `userIdFromRange(minId, maxId)` | A unique whole number from `minId` (inclusive) to `maxId` (exclusive). The range is split evenly between the agents, and each agent hands out its share in random order, each number once. When an agent's share runs out the function returns an empty string. | `#{stringFunctions.userIdFromRange(1, 1000)}` |

!!! note
    An expression ends at the first `}`, so an argument can't contain a literal `}`: `#{stringFunctions.substringBetween(body, '{', '}')}` doesn't work. Store `}` in a variable (say `closeBrace`) and pass that instead: `#{stringFunctions.substringBetween(body, '{', closeBrace)}`.

The legacy `#function.string...` forms, including the `useridFromRange` variants with include/exclude patterns and the response-body `substring`, are listed under [Legacy `#function` syntax](functions_variables.md#legacy-function-syntax).
