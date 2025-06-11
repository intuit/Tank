# Tank Integration Test Coverage

This document provides an overview of all integration test files, the controllers they test, and the API endpoints/functionality covered.

## Test Files Overview

### 1. DefaultApiIT.java
**Controller:** `DefaultController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/ping`

**APIs Covered:**
- `GET /v2/ping` - Basic ping endpoint

**Test Cases:**
- `testPingEndpoint_shouldReturnPongResponse()` - Basic ping functionality
- `testPingEndpointWithoutAuthorization()` - Authorization validation (401 expected)
- `testPingEndpointWithInvalidAuthorization()` - Invalid token handling (401 expected)
- `testPingEndpointPerformance_shouldRespondQuickly()` - Response time validation (<5s)
- `testPingEndpointMultipleRequests_shouldReturnConsistentResponses()` - Consistency testing (5 requests)
- `testPingEndpointWithCustomHeaders_shouldAcceptCustomHeaders()` - Custom header handling
- `testPingEndpointWithQueryParameters_shouldIgnoreQueryParams()` - Query parameter handling
- `testPingEndpointConcurrentRequests_shouldHandleConcurrency()` - Thread safety (3 concurrent requests)
- `testPingEndpointResponseHeaders_shouldReturnCorrectHeaders()` - Response header validation

### 2. ScriptApiIT.java
**Controller:** `ScriptController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/scripts`

**APIs Covered:**
- `GET /v2/scripts/ping` - Service health check
- `GET /v2/scripts` - Get all scripts
- `GET /v2/scripts/names` - Get script names map
- `GET /v2/scripts/{id}` - Get specific script
- `POST /v2/scripts/copy/{id}` - Copy script
- `POST /v2/scripts` - Upload script (multipart/form-data)
- `DELETE /v2/scripts/{id}` - Delete script

**Test Cases:**
- `testPingScriptService()` - Service availability
- `testGetAllScripts_shouldReturnScriptsList()` - List all scripts with structure validation
- `testGetAllScriptNames_shouldReturnScriptNamesMap()` - Script names mapping
- `testGetScriptById_shouldReturnScript()` - Individual script retrieval
- `testCopyScript_shouldCreateCopy()` - Script copying functionality
- `testUploadTankScript()` - Tank script upload (gzipped XML)
- `testUploadTankScriptGzipped()` - Gzipped script upload with proper headers
- `testUploadProxyRecording()` - Proxy recording upload (uncompressed XML)
- `testUploadProxyRecordingGzipped()` - Gzipped proxy recording upload
- `testDeleteScript_shouldDeleteSuccessfully()` - Script deletion

### 3. ProjectApiIT.java
**Controller:** `ProjectController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/projects`

**APIs Covered:**
- `GET /v2/projects/ping` - Service health check
- `GET /v2/projects` - Get all projects (disabled due to lazy loading issue)
- `GET /v2/projects/names` - Get project names map
- `GET /v2/projects/{id}` - Get specific project
- `POST /v2/projects/copy/{id}` - Copy project
- `GET /v2/projects/download/{id}` - Download project harness XML
- `DELETE /v2/projects/{id}` - Delete project

**Test Cases:**
- `testPingProjectService()` - Service availability
- `testGetAllProjects_shouldReturnProjectsList()` - List all projects (disabled) because of an issue with lazy loading
- `testGetAllProjectNames_shouldReturnProjectNamesMap()` - Project names mapping
- `testGetProjectById_shouldReturnProject()` - Individual project retrieval with structure validation
- `testCopyProject_shouldCreateCopy()` - Project copying functionality
- `testGetProjectHarnessXml_shouldReturnHarnessXml()` - XML harness download (404 test for non-existent)
- `testDeleteProject_shouldDeleteSuccessfully()` - Project deletion

### 4. JobApiIT.java
**Controller:** `JobController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/jobs`

**APIs Covered:**
- `GET /v2/jobs/ping` - Service health check
- `GET /v2/jobs` - Get all jobs
- `GET /v2/jobs/{id}` - Get specific job
- `POST /v2/jobs` - Create new job
- `POST /v2/jobs/{id}/action/{action}` - Job lifecycle actions (start, stop, pause, resume, kill)
- `GET /v2/jobs/{id}/status` - Get job status
- `DELETE /v2/jobs/{id}` - Delete job

**Test Cases:**
- `testPingJobService()` - Service availability
- `testGetAllJobs()` - List all jobs with structure validation
- `testGetJobById_shouldReturnJob()` - Individual job retrieval
- `testCreateJob_shouldCreateNewJob()` - Job creation using project ID 298
- `testJobLifecycleOperations()` - Complete lifecycle (create, start, pause, resume, stop, delete)
- `testGetJobStatus_shouldReturnStatus()` - Job status monitoring
- `testDeleteJob_shouldDeleteSuccessfully()` - Job deletion

### 5. AgentApiIT.java
**Controller:** `AgentController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/agent`

**APIs Covered:**
- `GET /v2/agent/ping` - Service health check
- `GET /v2/agent/settings` - Get agent settings XML
- `GET /v2/agent/headers` - Get agent headers
- `GET /v2/agent/clients` - Get client definitions
- `POST /v2/agent/ready` - Register agent as ready
- `GET /v2/agent/instance/{id}/status` - Get instance status
- `POST /v2/agent/instance/{id}/action/{action}` - Instance lifecycle actions

**Test Cases:**
- `testPingAgentService()` - Service availability
- `testGetAgentSettings()` - Agent settings XML retrieval
- `testGetAgentHeaders()` - Agent headers retrieval
- `testGetAgentClients()` - Client definitions retrieval
- `testAgentReadyEndpoint()` - Agent registration with job
- `testAgentInstanceOperationsWithRunningJob()` - Instance operations with real job context

### 6. DataFileApiIT.java
**Controller:** `DataFileController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/datafiles`

**APIs Covered:**
- `GET /v2/datafiles/ping` - Service health check
- `GET /v2/datafiles/names` - Get data file names map
- `GET /v2/datafiles` - Get all data files
- `GET /v2/datafiles/{id}` - Get specific data file

**Test Cases:**
- `testPingDataFileService()` - Service availability
- `testGetAllDataFileNames_shouldReturnDataFileNamesMap()` - Data file names mapping
- `testGetDataFileById_shouldReturnDataFile()` - Individual data file retrieval with structure validation

### 7. FilterApiIT.java
**Controller:** `FilterController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/filters`

**APIs Covered:**
- `GET /v2/filters/ping` - Service health check
- `GET /v2/filters` - Get all filters
- `GET /v2/filters/groups` - Get filter groups
- `POST /v2/filters/apply-filters/{scriptId}` - Apply filters to script

**Test Cases:**
- `testPingFilterService_shouldReturnPongResponse()` - Service availability
- `testGetAllFilters_shouldReturnFiltersList()` - List all filters with comprehensive validation
- `testGetAllFilterGroups_shouldReturnFilterGroupsList()` - Filter groups with specific group validation
- `testApplyFiltersToScript_shouldApplySuccessfully()` - Filter application using script copy, apply, and cleanup

### 8. LogApiIT.java
**Controller:** `LogController.java` (rest-mvc/impl)  
**Base Endpoint:** `/v2/logs`

**APIs Covered:**
- `GET /v2/logs/{filename}` - Get log file content

**Test Cases:**
- `testGetLogFile()` - Log file retrieval with proper content-type validation
- `testGetLogFileWithFromParameter()` - Log file with 'from' parameter for partial content
- `testGetNonExistentLogFile()` - 404 handling for non-existent files
- `testGetLogFileWithInvalidPath()` - Security validation against path traversal attacks

### Test Data Sources:
- **Project ID 298** - "Simple Endurance Project" for job testing
- **TTO UC1 files** - XML and GZ files from test_support/resources for script uploads
- **Filter IDs** - Known filter and filter group IDs for filter testing
- **Log filename format** - catalina.YYYY-MM-DD.log pattern

## Base Test Infrastructure

**BaseIT.java** - Provides common functionality:
- API token management (properties file or AWS SSM)
- HTTP client configuration with 30-second timeouts
- Common constants (QA_BASE_URL, headers, content types)
- Shared utilities for all integration tests
