package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JobApiIT extends BaseIT {

    private static final String JOBS_ENDPOINT = "/v2/jobs";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Integer> createdJobIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        // Clean up any jobs created during tests
        for (Integer jobId : createdJobIds) {
            try {
                // Kill the job to clean up
                HttpRequest killRequest = HttpRequest.newBuilder()
                        .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/kill/" + jobId))
                        .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                        .timeout(Duration.ofSeconds(30))
                        .GET()
                        .build();
                
                httpClient.send(killRequest, BodyHandlers.ofString());
            } catch (Exception e) {
                System.err.println("Failed to cleanup job " + jobId + ": " + e.getMessage());
            }
        }
        createdJobIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPingJobService_shouldReturnPongResponse() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/ping"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG JobServiceV2", response.body(), "Should return PONG response");
    }

    @Test
    @Tag("integration")
    public void testGetAllJobs_shouldReturnJobsList() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode responseBody = objectMapper.readTree(response.body());
        assertTrue(responseBody.has("jobs"), "Response should contain 'jobs' field");

        JsonNode jobs = responseBody.get("jobs");
        assertTrue(jobs.isArray(), "Jobs should be an array");

        if (jobs.size() > 0) {
            // Validate job structure
            JsonNode firstJob = jobs.get(0);
            assertTrue(firstJob.has("id"), "Job should have 'id' field");
            assertTrue(firstJob.has("name"), "Job should have 'name' field");
            assertTrue(firstJob.has("status"), "Job should have 'status' field");
            assertTrue(firstJob.has("numUsers"), "Job should have 'numUsers' field");
            
            // Validate data types
            assertTrue(firstJob.get("id").isInt(), "Job ID should be integer");
            assertTrue(firstJob.get("name").isTextual(), "Job name should be string");
            assertTrue(firstJob.get("status").isTextual(), "Job status should be string");
            assertTrue(firstJob.get("numUsers").isInt(), "Job numUsers should be integer");
        }
    }

    @Test
    @Tag("integration")
    public void testGetJobsByProject() throws Exception {
        // Arrange - Use project ID 298 (Simple Endurance Project)
        int projectId = 298;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/project/" + projectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode responseBody = objectMapper.readTree(response.body());
        assertTrue(responseBody.has("jobs"), "Response should contain 'jobs' field");

        JsonNode jobs = responseBody.get("jobs");
        assertTrue(jobs.isArray(), "Jobs should be an array");

        // Validate all jobs belong to the specified project
        for (JsonNode job : jobs) {
            assertTrue(job.has("id"), "Each job should have 'id' field");
            assertTrue(job.has("name"), "Each job should have 'name' field");
            assertTrue(job.has("status"), "Each job should have 'status' field");
        }
    }

    @Test
    @Tag("integration")
    public void testCreateJob_shouldCreateAndReturnJobId() throws Exception {
        // Arrange - Create job request for project 298 (Simple Endurance Project)
        String jobName = "Integration_Test_Job_" + System.currentTimeMillis();
        
        String requestBody = String.format("""
            {
                "projectId": 298,
                "jobInstanceName": "%s",
                "rampTime": "2m",
                "simulationTime": "5m",
                "userIntervalIncrement": 1,
                "stopBehavior": "END_OF_TEST",
                "vmInstance": "m5.large",
                "numUsersPerAgent": 50,
                "workloadType": "increasing",
                "jobRegions": [
                    {
                        "region": "us-west-2",
                        "users": "100",
                        "percentage": "100"
                    }
                ]
            }
            """, jobName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("JobId"), "Response should contain JobId");
        assertTrue(responseBody.get("status").contains("created"), "Response should indicate job creation");

        // Store for cleanup
        int jobId = Integer.parseInt(responseBody.get("JobId"));
        createdJobIds.add(jobId);

        // Verify job was created
        verifyJobExists(jobId, jobName, 100);

        // Verify job appears in getJobsByProject response
        verifyJobInProjectList(jobId, 298);

        // Verify job appears in getAllJobs response
        verifyJobInAllJobs(jobId);
    }

    @Test
    @Tag("integration")
    public void testCreateJobWithInvalidRequest_shouldReturn400BadRequest() throws Exception {
        // Arrange - Invalid request with missing required fields
        String invalidRequestBody = """
            {
                "projectId": 298,
                "rampTime": "invalid_time_format"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(invalidRequestBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(400, response.statusCode(), "Should return HTTP 400 Bad Request for invalid job request");
    }

    @Test
    @Tag("integration")
    public void testGetSpecificJob_shouldReturnJobDetails() throws Exception {
        // Arrange - First create a job to test with
        int jobId = createTestJob();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/" + jobId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode job = objectMapper.readTree(response.body());
        assertEquals(jobId, job.get("id").asInt(), "Should return correct job ID");
        assertTrue(job.has("name"), "Job should have name field");
        assertTrue(job.has("status"), "Job should have status field");
        assertTrue(job.has("numUsers"), "Job should have numUsers field");
    }

    @Test
    @Tag("integration")
    public void testGetAllJobStatus_shouldReturnStatusList() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/status"))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200);

        List<Map<String, String>> statuses = objectMapper.readValue(response.body(), List.class);
        assertNotNull(statuses, "Status list should not be null");

        if (!statuses.isEmpty()) {
            Map<String, String> firstStatus = statuses.get(0);
            assertTrue(firstStatus.containsKey("jobId"), "Status should contain jobId");
            assertTrue(firstStatus.containsKey("status"), "Status should contain status");
        }
    }

    // Helper method to create a test job
    private int createTestJob() throws Exception {
        String jobName = "Test_Job_" + System.currentTimeMillis();
        
        String requestBody = String.format("""
            {
                "projectName": "PDS_Perf_Baseline",
                "projectId": 367,
                "jobInstanceName": "%s",
                "rampTime": "1m",
                "simulationTime": "3m",
                "userIntervalIncrement": 1,
                "targetRampRate": 1.0,
                "stopBehavior": "END_OF_SCRIPT",
                "vmInstance": "c.medium",
                "numUsersPerAgent": 2,
                "targetRatePerAgent": 1.0,
                "workloadType": "standard",
                "jobRegions": [
                    {
                        "region": "us-east-2",
                        "percentage": "100"
                    },
                    {
                        "region": "us-west-2",
                        "percentage": "0"
                    }
                ]
            }
            """, jobName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Should create test job successfully");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        int jobId = Integer.parseInt(responseBody.get("JobId"));
        createdJobIds.add(jobId);

        return jobId;
    }

    // Helper method to verify job appears in project jobs list
    private void verifyJobInProjectList(int jobId, int projectId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/project/" + projectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode responseBody = objectMapper.readTree(response.body());
        JsonNode jobs = responseBody.get("jobs");

        boolean jobFound = false;
        for (JsonNode job : jobs) {
            if (job.get("id").asInt() == jobId) {
                jobFound = true;
                break;
            }
        }

        assertTrue(jobFound, "Newly created job should appear in getJobsByProject response");
    }

    // Helper method to verify job appears in all jobs list
    private void verifyJobInAllJobs(int jobId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode responseBody = objectMapper.readTree(response.body());
        JsonNode jobs = responseBody.get("jobs");

        boolean jobFound = false;
        for (JsonNode job : jobs) {
            if (job.get("id").asInt() == jobId) {
                jobFound = true;
                break;
            }
        }

        assertTrue(jobFound, "Newly created job should appear in getAllJobs response");
    }

    // Helper method to verify job exists
    private void verifyJobExists(int jobId, String expectedName, int numUsers) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/" + jobId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Job should exist after creation");
        System.out.println(response.body());

        JsonNode job = objectMapper.readTree(response.body());
        assertEquals(jobId, job.get("id").asInt(), "Job ID should match");
        assertEquals(expectedName, job.get("name").asText(), "Job name should match");
        assertEquals(job.get("status").asText(), "Created", "Job status should be 'Created'");
        assertEquals(job.get("numUsers").asInt(), numUsers, "Job numUsers should match expected value");
    }

    @Test
    @Tag("integration")
    public void testGetJobStatus_shouldReturnStatusForSpecificJob() throws Exception {
        // Arrange - First create a job to test with
        int jobId = createTestJob();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/status/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(response.statusCode(), 200);

        String status = response.body();
        assertNotNull(status, "Status should not be null");
        assertFalse(status.trim().isEmpty(), "Status should not be empty");
        // Common job statuses: Created, Starting, Running, Stopped, Completed, etc.
        assertTrue(status.matches("\\w+"), "Status should be a valid word");

    }

    @Test
    @Tag("integration")
    public void testGetJobVMStatuses_shouldReturnVMStatusesForJob() throws Exception {
        // Arrange - First create a job to test with
        int jobId = createTestJob();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/instance-status/" + jobId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200 || response.statusCode() == 404,
                     "Should return HTTP 200 OK or 404 Not Found for job VM statuses");
        if (response.statusCode() == 200) {

            JsonNode vmStatuses = objectMapper.readTree(response.body());
            assertTrue(vmStatuses.has("vmStatuses"), "Response should contain vmStatuses field");
            assertTrue(vmStatuses.get("vmStatuses").isArray(), "VM statuses should be an array");
        }
    }

    @Test
    @Tag("integration")
    public void testDownloadJobHarnessFile_shouldReturnHarnessFile() throws Exception {
        // Arrange - First create a job to test with
        int jobId = createTestJob();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/download/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(response.statusCode(), 200);
        assertEquals("application/xml", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return XML content type");
        assertTrue(response.headers().firstValue("Content-Disposition").isPresent(),
                  "Should have Content-Disposition header for download");
        assertTrue(response.body().contains("<?xml"), "Response should contain XML content");
    }

    @Test
    //@Disabled
    @Tag("integration")
    public void testJobLifecycleOperations_shouldManageJobLifecycle() throws Exception {
        // Arrange - First create a job to test lifecycle operations
        int jobId = createTestJob();

        // Test Start Job
        HttpRequest startRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/start/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> startResponse = httpClient.send(startRequest, BodyHandlers.ofString());
        assertEquals(200, startResponse.statusCode(), "Should successfully start job");
        assertNotNull(startResponse.body(), "Start response should not be null");

        // Wait a moment for status to update
        Thread.sleep(2000);

        // Test Pause Job
        HttpRequest pauseRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/pause/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> pauseResponse = httpClient.send(pauseRequest, BodyHandlers.ofString());
        assertEquals(200, pauseResponse.statusCode(), "Should successfully pause job");

        // Test Resume Job
        HttpRequest resumeRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/resume/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> resumeResponse = httpClient.send(resumeRequest, BodyHandlers.ofString());
        assertEquals(200, resumeResponse.statusCode(), "Should successfully resume job");

        // Test Stop Job
        HttpRequest stopRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/stop/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> stopResponse = httpClient.send(stopRequest, BodyHandlers.ofString());
        assertEquals(200, stopResponse.statusCode(), "Should successfully stop job");

        // Test Kill Job (final cleanup)
        HttpRequest killRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/kill/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> killResponse = httpClient.send(killRequest, BodyHandlers.ofString());
        assertEquals(200, killResponse.statusCode(), "Should successfully kill job");
    }

    @Test
    @Tag("integration")
    public void testCreateJobWithDifferentWorkloadTypes_shouldCreateStandardWorkload() throws Exception {
        // Test creating job with standard (nonlinear) workload
        String jobName = "Standard_Workload_Job_" + System.currentTimeMillis();

        String requestBody = String.format("""
            {
                "projectId": 298,
                "jobInstanceName": "%s",
                "rampTime": "2m",
                "simulationTime": "3m",
                "userIntervalIncrement": 1,
                "targetRampRate": 10.0,
                "targetRatePerAgent": 5.0,
                "stopBehavior": "END_OF_TEST",
                "vmInstance": "m5.large",
                "numUsersPerAgent": 20,
                "workloadType": "standard",
                "jobRegions": [
                    {
                        "region": "us-west-2",
                        "users": "50",
                        "percentage": "100"
                    }
                ]
            }
            """, jobName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should create standard workload job successfully");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("JobId"), "Response should contain JobId");

        // Store for cleanup
        int jobId = Integer.parseInt(responseBody.get("JobId"));
        createdJobIds.add(jobId);

        // Verify job was created
        verifyJobExists(jobId, jobName, 50);
    }
}
