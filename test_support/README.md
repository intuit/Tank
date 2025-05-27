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
- Common constants and utilities

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

### Test Environment

- **Target Environment**: `https://qa-tank.perf.a.intuit.com`
- **API Version**: v2
- **Authentication**: Bearer token

## Running the Tests

### Prerequisites
1. Valid API token (see Configuration section)
2. Network access to Tank QA environment
3. Maven 3.6+ and Java 17+

### Execute Tests

```bash
# Run all integration tests
By default, `mvn clean install` will run both unit and integration tests.
You can run individual tests directly on IntelliJ
```

### Test Tags
- `@Tag("integration")` - All integration tests
- Individual test methods can have additional specific tags

## Test Data Management

### Cleanup Strategy
- Tests create temporary scripts with unique names
- Consider implementing cleanup in `@AfterEach` or `@AfterAll` methods
- Use descriptive names to identify test-created resources