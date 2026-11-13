# Using the REST API

The Tank controller has a JSON REST API, version 2, under `/v2`. The web UI uses it, and scripts and CI jobs can use it too. Every endpoint is listed in the [API Reference](reference.md).

## Base URL
Requests go to the controller's base URL followed by `/v2/...`. The installation guide deploys Tank as Tomcat's `ROOT.war`, so the base URL is the host, for example `https://tank.example.com`. If you deployed `tank.war` under its own name, add the context path: `https://tank.example.com/tank`. The examples below use `$TANK` for it.

```bash
TANK=https://tank.example.com
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" "$TANK/v2/ping"
```

Bodies are JSON unless an endpoint says otherwise. File downloads (scripts, harness XML, tools) and a few plain-text responses such as the `ping` endpoints are the exceptions; the reference shows each endpoint's media types.

## Authentication
The API accepts two kinds of credentials.

### API token
Send a user's API token as a bearer token:

```bash
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" "$TANK/v2/me"
```

The request runs as that user, with that user's groups and rights. This is the method to use from scripts and CI.

Create the token in the web UI, on your account page (`Account Settings` in the classic UI), or call `POST /v2/me/api-token` from a signed-in session. Each user has one token: generating a new one replaces the old one, and the token is only shown when it is generated. `DELETE /v2/me/api-token` removes it.

An invalid token is never replaced by a session login: the request is treated as having no credentials (see [Unauthenticated requests](#unauthenticated-requests)).

!!! note "The agent token"
    Agents authenticate with the controller's agent token, also as `Authorization: Bearer`. It is meant for agents, not for people or scripts. Operations that act as a signed-in user, such as `/v2/me` and `/v2/tools/...`, refuse it with 403.

### Session login
The web UI logs in with a session cookie. You can do the same with `curl`:

```bash
# Log in: sets the session cookie and an XSRF-TOKEN cookie
curl -fsS -c cookies.txt -H "Content-Type: application/json" \
  -d '{"username":"<user>","password":"<password>"}' \
  "$TANK/v2/auth/login"

# Read with the session
curl -fsS -b cookies.txt "$TANK/v2/me"
```

State-changing requests (anything but `GET`, `HEAD`, `OPTIONS` and `TRACE`) made with a session must send the value of the `XSRF-TOKEN` cookie in the `X-XSRF-TOKEN` header, or they are rejected with 403 "Missing or invalid CSRF token". Requests made with an API token don't need it.

```bash
XSRF=$(awk '$6 == "XSRF-TOKEN" {print $7}' cookies.txt)
curl -fsS -b cookies.txt -H "X-XSRF-TOKEN: $XSRF" -X POST "$TANK/v2/me/api-token"
```

`POST /v2/auth/logout` ends the session. When single sign-on is configured, the browser logs in through `GET /v2/auth/sso/authorize` instead; `GET /v2/auth/config` tells a client whether SSO is available.

### Unauthenticated requests
Only `/v2/auth/config`, `/v2/auth/login`, `/v2/auth/sso/authorize` and `/v2/auth/sso/callback` are always open.

Whether the rest of the API requires credentials depends on `rest-security-enabled` in the controller's `settings.xml`:

- `true`: any other request without valid credentials gets 401.
- `false` (the default in the shipped `settings.xml`): requests without credentials are allowed and run with full rights, except operations that need a signed-in user, such as `/v2/me` and `/v2/tools/...`, which still return 401.

With credentials, each endpoint checks the caller's rights (see [Roles](../roles.md)) and returns 403 when the caller lacks them.

## Errors
Errors from the API's own checks have a JSON body (`ErrorResponse` in the reference):

```json
{
  "message": "Resource not found: ...",
  "debugInfo": null
}
```

| Field | Meaning |
|---|---|
| `message` | What went wrong, for showing to a user |
| `debugInfo` | The stack trace when the server sets `server.include-debug-info: true`; otherwise `null` |

The status code carries the kind of error: 400 for a bad request or parameter, 401 when not signed in, 403 when not allowed, 404 when not found, 409 for a conflict, and 500 for a server error. Authentication failures caught before a request reaches the API (a bad token, a missing CSRF token) have only `message`, for example `{"message":"Unauthorized"}`.

Framework errors return a plain-text message instead of JSON, for example `Incorrect request body` (400) when the body can't be read, `File upload error: incorrect request or missing file parameter` (400), or `Not Found` (404) for a path that doesn't exist.

## Paging
These list endpoints can return one page at a time:

| Endpoint | Default sort |
|---|---|
| `GET /v2/projects` | `modified,desc` |
| `GET /v2/scripts` | `modified,desc` |
| `GET /v2/datafiles` | `modified,desc` |
| `GET /v2/admin/users` (always paged) | `name,asc` |

| Parameter | Meaning |
|---|---|
| `page` | Zero-based page number. On projects, scripts and data files it selects the paged form: without it, the endpoint returns every item unpaged |
| `size` | Page size, 1 to 200 (default 25) |
| `sort` | A field name, optionally followed by `,asc` or `,desc`. The reference lists the fields each endpoint accepts |
| `q` | Text to search for (in the name, product or comments; for users, the name or email) |
| `owner` | Only items owned by this user (not on users) |

A paged response looks like this:

```json
{
  "items": [ ... ],
  "page": 0,
  "size": 25,
  "total": 132
}
```

An invalid `page`, `size` or `sort` returns 400.

## Examples
List the 10 most recently modified projects whose name, product or comments contain `checkout`:

```bash
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" \
  "$TANK/v2/projects?page=0&size=10&sort=modified,desc&q=checkout"
```

Get one project, then download its harness XML file:

```bash
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" "$TANK/v2/projects/42"
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" -OJ "$TANK/v2/projects/download/42"
```

Download the agent visual debugger built with this controller (see [Supplemental Tools](../supplemental-tools.md)):

```bash
curl -fsS -H "Authorization: Bearer $TANK_TOKEN" -o Tank-Debugger-all.jar "$TANK/v2/tools/debugger"
```
