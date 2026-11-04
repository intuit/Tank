# Tank Integration Tests

This module contains integration tests for the Tank application, focusing on testing the REST API endpoints against the QA Tank environment.

## Overview

The integration tests are designed to test the Tank application's REST API functionality end-to-end, including:

- Script upload and management
- Project operations
- Job management
- Agent operations

## Test Structure

### Base Test Class

`BaseIT.java` - Provides common functionality for all integration tests:
- API token management (from properties file or AWS SSM)
- HTTP client configuration
- Request helpers (`send`, `get`, `sendAnonymous`, `postFiles`, `expect`) and shared fixtures (`createRunnableProject`,
  `currentUserName`, `uniqueName`) used by the React migration tests
- The helpers send `Accept: application/json, */*;q=0.1`: JSON where an endpoint offers it, otherwise whatever it
  returns. Several endpoints only produce `text/plain` (the single-item deletes, data file content, log files,
  step responses) and answer a JSON-only `Accept` with 406, without running.

### React migration endpoint tests

These cover the `/v2` endpoints added for the React UI:

| Class | Endpoints |
|-------|-----------|
| `SessionApiIT` | `/v2/auth/config`, `/v2/auth/login`, `/v2/me`, `/v2/me/preferences`, `/v2/config/options`, `/v2/users/names` |
| `ProjectEditorApiIT` | Paged `/v2/projects`, `/v2/projects/{id}/full` (GET/PUT, 409 on stale saves), `/validate`, `/copy`, bulk delete |
| `JobQueueApiIT` | `/v2/projects/{id}/jobs/preview`, queueing, `/v2/jobs/tree`, `/details`, the time series, job delete |
| `DataFileBatchApiIT` | `/v2/datafiles/batch` (files and zips), paged `/v2/datafiles`, `/preview`, `X-Total-Lines`, bulk delete |
| `ScriptEditorApiIT` | Paged `/v2/scripts`, `/v2/scripts/{id}/steps` (GET/PUT, 409 on stale saves), `/copy`, step responses, `/v2/scripts/steps/{search,replace,apply-filters,validate}`, `/v2/scripts/logic/test`, recording uploads with `productName` and `filterIds` |
| `FilterEditorApiIT` | `PUT /v2/filters/{id}`, filter and group `/copy`, `POST`/`PUT /v2/filters/groups`, group cleanup on filter delete, `filterActionFields` in `/v2/config/options` |
| `AdminApiIT` | `/v2/admin/users` (CRUD, owned-project conflict, self-delete), user API tokens (including a non-admin token getting 403), preferences reset, `/v2/admin/groups`, `/v2/admin/logs`, `/v2/admin/log-level` |

They follow these rules so they are safe to run against the shared QA instance:
- Jobs are only queued, never started, so no agents launch. Each one is deleted while it is still Created.
- Nothing calls `POST/DELETE /v2/me/api-token` or `PUT /v2/me`, which would replace the token or change the
  account every test runs as. A changed column preference is put back afterwards.
- Logic step tests only send scripts that finish at once. A script that never ends keeps a thread busy on the
  controller after the 5 second limit.
- `AdminApiIT` needs the test user to be an admin and is skipped otherwise. It creates users named
  `it-admin-...` and deletes them, and only sets the log level to the level it already has.

Project tests run QA script 1, as the other project tests do.

## Configuration

### API Token Configuration

The tests support two methods for API token configuration:

#### 1. Local Development (Properties File)
Create `test_support/src/test/resources/test-config.properties`:
```properties
tank.api.token=your_api_token_here
```

#### 2. CI/CD Pipeline (AWS SSM Parameter Store)
The tests automatically fetch the token from AWS SSM Parameter Store:
- Parameter: `/Tank/qa/integration-tests/api/token`
- Requires appropriate AWS credentials/IAM permissions
- The SSM client uses the default region lookup, so set the region if your profile has none:
  `export AWS_REGION=us-east-2`

The properties file wins when both are present. Tests run as whichever user the token belongs to, so don't
assume a particular user name.

### Test Environment

- **Target Environment**: `https://qa-tank.perf.a.intuit.com` (`BaseIT.QA_BASE_URL`)
- **API Version**: v2
- **Authentication**: Bearer token

## Running the Tests

### Prerequisites
1. Valid API token (see Configuration section)
2. Access to Tank QA environment with a valid API token

### Execute Tests

`mvn clean install` and `mvn verify` do **not** run these tests: this module has no parent POM and doesn't bind
the failsafe plugin, and Surefire's default includes don't match `*IT` classes. Name them with `-Dtest` instead:

```bash
# All integration tests (about 2.5 minutes)
mvn -pl test_support test -Dtest='*IT'

# One class
mvn -pl test_support test -Dtest=SessionApiIT

# One method
mvn -pl test_support test -Dtest='JobQueueApiIT#testQueueJob_appearsInTreeAndDetailsThenDeletes'
```

Results are in `test_support/target/surefire-reports/`. You can also run a class or method from IntelliJ or
another IDE.

### Test Tags
- `@Tag("integration")` - All integration tests
- Individual test methods can have additional specific tags

## Test Data Management

### Cleanup Strategy
- Name created resources with `uniqueName(...)` (or a per-run prefix), so runs don't collide and leftovers are easy
  to spot.
- Record each created ID and delete it in `@AfterEach`, ignoring failures so one bad delete doesn't hide the
  test's own result. See `DataFileBatchApiIT.cleanup()`.
- Check that cleanup actually worked when adding a new kind of resource: a delete that fails quietly leaves data
  on the shared QA instance.

## Writing Tests

- Use the `BaseIT` helpers rather than building requests by hand, so every test sends the same headers.
- Compare owners and creators with `currentUserName()`, not a fixed name.
- Don't depend on the order of lists that come from QA's configuration, such as the regions in
  `/v2/projects/{id}/full`. Find the entry you need by its name.
- To save after creating something, send back the `modified` from the response (or from a fresh GET). The server
  treats times less than a second apart as the same, because the database may round milliseconds.