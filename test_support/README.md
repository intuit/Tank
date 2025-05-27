# Tank Integration Tests

This module contains integration tests for the Tank application, focusing on testing the REST API endpoints against a live Tank environment.

## Overview

The integration tests are designed to test the Tank application's REST API functionality end-to-end, including:

- Script upload and management
- Project operations
- Data file operations
- Job management
- Agent operations

## Test Structure

### Base Test Class

`BaseIT.java` - Provides common functionality for all integration tests:
- API token management (from properties file or AWS SSM)
- HTTP client configuration
- Common constants and utilities

### Script Upload Integration Tests

`ScriptUploadIT.java` - Tests script upload functionality:

#### Test Cases Covered:

1. **Tank Script Upload** (`testUploadNewTankScript`)
   - Uploads a Tank XML script file
   - Verifies successful creation (HTTP 201)
   - Validates response contains script ID
   - Confirms script can be retrieved

2. **Gzipped Script Upload** (`testUploadGzippedScript`)
   - Tests compressed script upload
   - Uses `Content-Encoding: gzip` header
   - Validates decompression and processing

3. **Proxy Recording Upload** (`testUploadProxyRecording`)
   - Tests Tank Proxy recording upload
   - Uses `recording` parameter
   - Different XML format than Tank scripts

4. **Script Copying** (`testCopyExistingScript`)
   - Tests copying existing scripts
   - Uses `copy` and `sourceId` parameters
   - Validates new script creation

5. **Invalid XML Handling** (`testUploadInvalidXml`)
   - Tests error handling for malformed XML
   - Validates proper error responses

6. **Script Download** (`testDownloadScript`)
   - Tests script download functionality
   - Validates XML content and headers

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

## Sample Test Files

### Tank Script Format (`sample-tank-script.xml`)
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<script xmlns="urn:com/intuit/tank/script/v1">
    <id>0</id>
    <name>Sample Integration Test Script</name>
    <creator>integration-test</creator>
    <steps>
        <!-- HTTP requests, sleep, variables, etc. -->
    </steps>
</script>
```

### Proxy Recording Format (`sample-proxy-recording.xml`)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<testPlan name="ProxyRecordingIntegrationTest">
    <testSuite name="MainSuite">
        <useCase name="LoginFlow">
            <request method="GET" url="http://example.com/login">
                <!-- Headers, body, etc. -->
            </request>
        </useCase>
    </testSuite>
</testPlan>
```

## Running the Tests

### Prerequisites
1. Valid API token (see Configuration section)
2. Network access to Tank QA environment
3. Maven 3.6+ and Java 11+

### Execute Tests

```bash
# Run all integration tests
mvn test -pl test_support -Dtest="*IT"

# Run only script upload tests
mvn test -pl test_support -Dtest="ScriptUploadIT"

# Run with specific tag
mvn test -pl test_support -Dtest="*IT" -Dgroups="integration"
```

### Test Tags
- `@Tag("integration")` - All integration tests
- Individual test methods can have additional specific tags

## Test Data Management

### Cleanup Strategy
- Tests create temporary scripts with unique names
- Consider implementing cleanup in `@AfterEach` or `@AfterAll` methods
- Use descriptive names to identify test-created resources

### Test Isolation
- Each test should be independent
- Use unique identifiers (timestamps, UUIDs) for test data
- Avoid dependencies between test methods

## Troubleshooting

### Common Issues

1. **Authentication Failures (401)**
   - Verify API token is valid and not expired
   - Check token format (should include "Bearer " prefix)

2. **Network Timeouts**
   - Increase timeout values in test configuration
   - Check network connectivity to QA environment

3. **Test Data Conflicts**
   - Ensure unique naming for test scripts
   - Check for leftover test data from previous runs

4. **XML Parsing Errors**
   - Validate sample XML files are well-formed
   - Check namespace declarations match Tank expectations

### Debugging Tips

1. **Enable Detailed Logging**
   ```java
   // Add to test methods for debugging
   System.out.println("Response: " + response.body());
   System.out.println("Headers: " + response.headers().map());
   ```

2. **Validate API Responses**
   - Check response status codes
   - Examine response headers
   - Validate JSON response structure

3. **Test Against Local Environment**
   - Modify `QA_BASE_URL` to point to local Tank instance
   - Useful for debugging without affecting QA environment

## Best Practices

1. **Test Naming**: Use descriptive test method names that clearly indicate what is being tested
2. **Assertions**: Include meaningful assertion messages
3. **Test Data**: Use realistic but safe test data
4. **Error Handling**: Test both success and failure scenarios
5. **Documentation**: Keep this README updated as tests evolve

## Future Enhancements

1. **Additional Test Coverage**:
   - Bulk script operations
   - Script validation edge cases
   - Performance testing for large files
   - Concurrent upload scenarios

2. **Test Infrastructure**:
   - Parameterized tests for different environments
   - Test data factories for complex scenarios
   - Custom assertions for Tank-specific validations

3. **CI/CD Integration**:
   - Automated test execution in build pipeline
   - Test result reporting and notifications
   - Environment-specific test configurations
