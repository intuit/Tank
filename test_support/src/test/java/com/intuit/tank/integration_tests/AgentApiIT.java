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

public class AgentApiIT extends BaseIT {

    private static final String AGENT_ENDPOINT = "/v2/agent";
    private static final String JOBS_ENDPOINT = "/v2/jobs";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Integer> createdJobIds = new ArrayList<>();
    private final List<String> testInstanceIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        // Clean up any jobs created during tests
        for (Integer jobId : createdJobIds) {
            try {
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
        testInstanceIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPingAgentService_shouldReturnPongResponse() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/ping"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG AgentServiceV2", response.body(), "Should return PONG response");
    }

    @Test
    @Tag("integration")
    public void testGetAgentSettings() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/settings"))
                .header("Accept", "application/xml")
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200, "Should return HTTP 200 OK");
        assertEquals("application/xml;charset=UTF-8", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return XML content type");
        assertTrue(response.body().contains("<turboScale-settings>"), "Response should contain XML content");
    }

    @Test
    @Tag("integration")
    public void testGetAgentSupportFiles() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/support-files"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200, "Should return HTTP 200 OK");

        assertEquals("application/octet-stream",
                    response.headers().firstValue("Content-Type").orElse(""),
                    "Should return application/octet-stream content type");
        assertTrue(response.headers().firstValue("Content-Disposition").isPresent(),
                  "Should have Content-Disposition header for download");
        assertNotNull(response.body(), "Support files content should not be null");
    }

    @Test
    @Tag("integration")
    public void testGetAgentHeaders() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/headers"))
                .header("Accept", "application/xml")
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200,"Should return HTTP 200 OK");

        assertEquals("application/xml", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return XML content type");
        assertTrue(response.body().contains("<?xml"), "Response should contain XML content");
        assertTrue(response.body().contains("headers"), "Response should contain headers element");
    }

    @Test
    @Tag("integration")
    public void testGetAgentClients() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/clients"))
                .header("Accept", "application/xml")
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200, "Should return HTTP 200 OK");
        assertEquals("application/xml", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return XML content type");
        assertTrue(response.body().contains("<?xml"), "Response should contain XML content");
        assertTrue(response.body().contains("client"), "Response should contain client definitions");
    }

    @Test
    @Tag("integration")
    public void testAgentReadyEndpoint() throws Exception {
        // Arrange - First create and start a job to get a real job ID
        int jobId = createAndStartTestJob();

        // Wait a moment for job to start
        Thread.sleep(2000);

        // Create agent data payload with the real job ID
        String instanceId = "test-instance-" + System.currentTimeMillis();
        String agentDataJson = String.format("""
            {
                "jobId": "%d",
                "instanceId": "%s",
                "instanceUrl": "http://test-agent.example.com",
                "region": "US_WEST_2",
                "capacity": 100,
                "users": 50,
                "zone": "us-west-2a"
            }
            """, jobId, instanceId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/ready"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(agentDataJson))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 200, "Should return HTTP 200 OK");
        JsonNode responseBody = objectMapper.readTree(response.body());
        assertTrue(responseBody.has("jobId"), "Response should contain jobId");
        assertTrue(responseBody.has("rampTime"), "Response should contain rampTime");
        assertTrue(responseBody.has("totalAgents"), "Response should contain totalAgents");
        assertTrue(responseBody.has("concurrentUsers"), "Response should contain concurrentUsers");

        // Verify the response contains the correct job ID
        assertEquals(String.valueOf(jobId), responseBody.get("jobId").asText(),
                    "Response jobId should match the created job ID");
    }

    @Test
    @Tag("integration")
    public void testGetInstanceStatusWithNonExistentInstance() throws Exception {
        // Arrange
        String nonExistentInstanceId = "non-existent-instance-" + System.currentTimeMillis();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/status/" + nonExistentInstanceId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent instance");
    }

    @Test
    @Tag("integration")
    public void testAgentInstanceOperationsWithRunningJob_shouldManageInstanceLifecycle() throws Exception {
        // Arrange - First create and start a job to get real instance IDs
        int jobId = createAndStartTestJob();

        // Wait a moment for job to start and create instances
        Thread.sleep(5000);

        // Get VM statuses to find instance IDs
        List<String> instanceIds = getInstanceIdsFromJob(jobId);
        String instanceId = instanceIds.get(0);
        testInstanceIds.add(instanceId);

        // Test get instance status
        testGetInstanceStatus(instanceId);

        // Test instance lifecycle operations
        testInstanceLifecycleOperations(instanceId);
    }

    private void testGetInstanceStatus(String instanceId) throws Exception {
        // Test get instance status
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/status/" + instanceId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        assertTrue(response.statusCode() == 200,"Should return HTTP 200 OK");

        JsonNode instanceStatus = objectMapper.readTree(response.body());
        assertTrue(instanceStatus.has("instanceId"), "Response should contain instanceId");
        assertTrue(instanceStatus.has("jobId"), "Response should contain jobId");
        assertTrue(instanceStatus.has("jobStatus"), "Response should contain jobStatus");
        assertTrue(instanceStatus.has("vmStatus"), "Response should contain vmStatus");
        assertTrue(instanceStatus.has("vmRegion"), "Response should contain vmRegion");

        assertEquals(instanceId, instanceStatus.get("instanceId").asText(),
                    "Instance ID should match requested ID");
    }

    private void testInstanceLifecycleOperations(String instanceId) throws Exception {
        // Test pause instance
        HttpRequest pauseRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/pause/" + instanceId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> pauseResponse = httpClient.send(pauseRequest, BodyHandlers.ofString());
        assertTrue(pauseResponse.statusCode() == 200 || pauseResponse.statusCode() == 400,
                  "Should return HTTP 200 OK or 400 for pause operation");

        if (pauseResponse.statusCode() == 200) {
            assertNotNull(pauseResponse.body(), "Pause response should not be null");
        }

        // Test resume instance
        HttpRequest resumeRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/resume/" + instanceId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> resumeResponse = httpClient.send(resumeRequest, BodyHandlers.ofString());
        assertTrue(resumeResponse.statusCode() == 200 || resumeResponse.statusCode() == 400,
                  "Should return HTTP 200 OK or 400 for resume operation");

        assertNotNull(resumeResponse.body(), "Resume response should not be null");
        // Test stop instance
        HttpRequest stopRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/stop/" + instanceId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> stopResponse = httpClient.send(stopRequest, BodyHandlers.ofString());
        assertTrue(stopResponse.statusCode() == 200);

        assertNotNull(stopResponse.body(), "Stop response should not be null");
    }

    @Test
    @Tag("integration")
    public void testSetInstanceStatus() throws Exception {
        // Arrange - Create a test instance status payload
        String testInstanceId = "test-instance-" + System.currentTimeMillis();
        String instanceStatusJson = """
            {
                "instanceId": "%s",
                "jobId": "12345",
                "securityGroup": "test-security-group",
                "jobStatus": "Running",
                "role": "AGENT",
                "vmRegion": "US_WEST_2",
                "vmStatus": "running",
                "totalUsers": 100,
                "currentUsers": 50
            }
            """.formatted(testInstanceId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/status/" + testInstanceId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .PUT(HttpRequest.BodyPublishers.ofString(instanceStatusJson))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertTrue(response.statusCode() == 202);
    }

    @Test
    @Tag("integration")
    public void testKillInstanceWithNonExistentInstance() throws Exception {
        // Arrange
        String nonExistentInstanceId = "non-existent-instance-" + System.currentTimeMillis();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/instance/kill/" + nonExistentInstanceId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(response.statusCode(), 400);
    }

    @Test
    @Tag("integration")
    public void testSetStandaloneAgentAvailabilityWithInvalidData() throws Exception {
        // Arrange - Create invalid availability payload (missing required fields)
        String invalidAvailabilityJson = """
            {
                "capacity": 200
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + AGENT_ENDPOINT + "/availability"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(invalidAvailabilityJson))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(400, response.statusCode(), "Should return HTTP 400 Bad Request for invalid availability data");
    }

    // Helper method to create and start a test job
    private int createAndStartTestJob() throws Exception {
        // Create job
        String jobName = "Agent_Test_Job_" + System.currentTimeMillis();
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

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> createResponse = httpClient.send(createRequest, BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode(), "Should create job successfully");

        Map<String, String> responseBody = objectMapper.readValue(createResponse.body(), Map.class);
        int jobId = Integer.parseInt(responseBody.get("JobId"));
        createdJobIds.add(jobId);

        // Start job
        HttpRequest startRequest = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/start/" + jobId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> startResponse = httpClient.send(startRequest, BodyHandlers.ofString());
        assertEquals(200, startResponse.statusCode(), "Should start job successfully");

        return jobId;
    }

    // Helper method to get instance IDs from a running job
    private List<String> getInstanceIdsFromJob(int jobId) throws Exception {
        List<String> instanceIds = new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + JOBS_ENDPOINT + "/instance-status/" + jobId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        JsonNode vmStatuses = objectMapper.readTree(response.body());
        if (vmStatuses.has("statuses")) {
            JsonNode statuses = vmStatuses.get("statuses");
            if (statuses.isArray()) {
                for (JsonNode status : statuses) {
                    if (status.has("instanceId")) {
                        instanceIds.add(status.get("instanceId").asText());
                    }
                }
            }
        }

        return instanceIds;
    }
}
