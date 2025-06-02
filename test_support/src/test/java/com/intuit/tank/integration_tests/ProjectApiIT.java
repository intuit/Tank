package com.intuit.tank.integration_tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectApiIT extends BaseIT {

    private static final String PROJECTS_ENDPOINT = "/v2/projects";
    private final List<Integer> createdProjectIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        // Clean up any projects created during tests
        for (Integer projectId : createdProjectIds) {
            try {
                deleteProject(projectId);
            } catch (Exception e) {
                // Log but don't fail the test if cleanup fails
                System.err.println("Failed to cleanup project " + projectId + ": " + e.getMessage());
            }
        }
        createdProjectIds.clear();
    }

    @Test
    @Tag("integration")
    public void testPingProjectService() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/ping"))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("PONG ProjectServiceV2", response.body(), "Should return PONG response");
    }

    @Test
    @Disabled
    @Tag("integration")
    // This test does not work as there is an issue with findAll() method
    // It lazily fetches test plans, however, it accesses test plans after closing the session, causing an exception
    public void testGetAllProjects() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT))
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
        assertTrue(responseBody.has("projects"), "Response should contain 'projects' field");

        JsonNode projects = responseBody.get("projects");
        assertTrue(projects.isArray(), "Projects should be an array");
        assertTrue(projects.size() > 0, "Should contain at least one project");

        // Validate project structure
        JsonNode firstProject = projects.get(0);
        assertTrue(firstProject.has("id"), "Project should have 'id' field");
        assertTrue(firstProject.has("name"), "Project should have 'name' field");
        assertTrue(firstProject.has("creator"), "Project should have 'creator' field");
    }

    @Test
    @Tag("integration")
    public void testGetProjectNames() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + "/v2/projects/names"))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        Map<String, String> projectNames = objectMapper.readValue(response.body(), Map.class);
        assertTrue(projectNames.containsValue("Simple Endurance Project"),
                "Response should contain 'Simple Endurance Project'");
        assertTrue(projectNames.containsValue("PDS_Perf_Baseline"),
                "Response should contain 'PDS_Perf_Baseline'");

        assertTrue(projectNames.size() > 10,
                "Response should contain multiple projects (at least 10)");

        assertEquals(298, Integer.parseInt(projectNames.entrySet().stream()
                .filter(entry -> "Simple Endurance Project".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null)), "Should find project ID for 'Simple Endurance Project'");

        assertEquals(367, Integer.parseInt(projectNames.entrySet().stream()
                    .filter(entry -> "PDS_Perf_Baseline".equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null)), "Should find project ID for 'PDS_Perf_Baseline'");
    }

    @Test
    @Tag("integration")
    public void testGetSpecificProject() throws Exception {
        // Arrange - Use a known project ID
        int projectId = 298; // Simple Endurance Project

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        JsonNode project = objectMapper.readTree(response.body());
        assertEquals(projectId, project.get("id").asInt(), "Should return correct project ID");
        assertEquals("Simple Endurance Project", project.get("name").asText(), "Should return correct project name");
        assertTrue(project.has("creator"), "Project should have creator field");
        assertTrue(project.has("created"), "Project should have created field");
        assertTrue(project.has("modified"), "Project should have modified field");
    }

    @Test
    @Tag("integration")
    public void testGetNonExistentProject() throws Exception {
        // Arrange - Use a non-existent project ID
        int nonExistentProjectId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + nonExistentProjectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent project");
    }

    @Test
    @Tag("integration")
    public void testCreateProject() throws Exception {
        // Arrange
        String projectName = "Integration Test Project " + System.currentTimeMillis();
        String requestBody = createProjectRequestJson(projectName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("ProjectId"), "Response should contain ProjectId");
        assertEquals(responseBody.get("status"), ("Created"), "Response should indicate project was created");

        // Store for cleanup
        int projectId = Integer.parseInt(responseBody.get("ProjectId"));
        createdProjectIds.add(projectId);

        // Verify project was created by fetching it
        verifyProjectExists(projectId, projectName);
    }

    @Test
    @Tag("integration")
    public void testCreateProjectWithComplexConfiguration() throws Exception {
        // Arrange
        String projectName = "Complex Integration Test Project " + System.currentTimeMillis();
        String requestBody = createComplexProjectRequestJson(projectName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(201, response.statusCode(), "Should return HTTP 201 Created");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertNotNull(responseBody.get("ProjectId"), "Response should contain ProjectId");

        // Store for cleanup
        int projectId = Integer.parseInt(responseBody.get("ProjectId"));
        createdProjectIds.add(projectId);

        // Verify project configuration
        verifyComplexProjectConfiguration(projectId, projectName);
    }

    @Test
    @Tag("integration")
    public void testUpdateProject() throws Exception {
        // Arrange - First create a project
        String originalName = "Update Test Project " + System.currentTimeMillis();
        int projectId = createTestProject(originalName);

        String updatedName = "Updated " + originalName;
        String updateRequestBody = createProjectUpdateRequestJson(updatedName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequestBody, StandardCharsets.UTF_8))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        assertEquals(String.valueOf(projectId), responseBody.get("ProjectId"), "Response should contain correct ProjectId");
        assertTrue(responseBody.get("status").contains("Updated"), "Response should indicate project was updated");

        // Verify project was updated
        verifyProjectExists(projectId, updatedName);
    }

    @Test
    @Tag("integration")
    public void testUpdateNonExistentProject() throws Exception {
        // Arrange
        int nonExistentProjectId = 999999;
        String updateRequestBody = createProjectUpdateRequestJson("Non-existent Project");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + nonExistentProjectId))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .PUT(HttpRequest.BodyPublishers.ofString(updateRequestBody, StandardCharsets.UTF_8))
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(400, response.statusCode(), "Should return HTTP 400 Bad Request for non-existent project");
    }

    @Test
    @Tag("integration")
    public void testDownloadProjectHarnessFile() throws Exception {
        // Arrange - Use a known project ID that has test plans
        int projectId = 298; // Simple Endurance Project

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/download/" + projectId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert - Basic HTTP Response Validation
        assertEquals(200, response.statusCode(), "Should return HTTP 200 OK");
        assertEquals("application/xml", response.headers().firstValue("Content-Type").orElse(""),
                    "Should return XML content type");
        assertTrue(response.headers().firstValue("Content-Disposition").isPresent(),
                  "Should have Content-Disposition header");

        String contentDisposition = response.headers().firstValue("Content-Disposition").orElse("");
        assertTrue(contentDisposition.contains("attachment"), "Content-Disposition should indicate attachment");
        assertTrue(contentDisposition.contains("filename"), "Content-Disposition should contain filename");

        // Assert - XML Structure and Content Validation
        String xmlContent = response.body();
        assertNotNull(xmlContent, "Response body should not be null");
        assertFalse(xmlContent.trim().isEmpty(), "Response body should not be empty");

        // Validate XML is well-formed and parse it
        Document doc = parseXmlDocument(xmlContent);
        assertNotNull(doc, "XML should be well-formed and parseable");

        // Validate root element and namespace
        Element rootElement = doc.getDocumentElement();
        assertEquals("workload", rootElement.getLocalName(), "Root element should be 'workload'");
        assertEquals("urn:com/intuit/tank/harness/data/v1", rootElement.getNamespaceURI(),
                    "Should have correct Tank harness namespace");

        // Validate workload attributes
        assertTrue(rootElement.hasAttribute("name"), "Workload should have 'name' attribute");
        assertTrue(rootElement.getAttribute("name").contains("Simple Endurance Project"),
                  "Workload name should contain project name");
        assertTrue(rootElement.getAttribute("name").contains("id298"),
                  "Workload name should contain project ID");

        // Validate essential workload structure
        validateWorkloadStructure(doc);
        validateVariablesSection(doc);
        validateTestPlanStructure(doc);
        validateClientConfiguration(doc);
    }

    @Test
    @Tag("integration")
    public void testDownloadNonExistentProjectHarnessFile() throws Exception {
        // Arrange
        int nonExistentProjectId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/download/" + nonExistentProjectId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent project");
    }

    @Test
    @Tag("integration")
    public void testDeleteProject() throws Exception {
        // Arrange - Create a project to delete
        String projectName = "Delete Test Project " + System.currentTimeMillis();
        int projectId = createTestProject(projectName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(204, response.statusCode(), "Should return HTTP 204 No Content");

        // Remove from cleanup list since it's already deleted
        createdProjectIds.remove(Integer.valueOf(projectId));

        // Verify project was deleted
        verifyProjectDeleted(projectId);
    }

    @Test
    @Tag("integration")
    public void testDeleteNonExistentProject() throws Exception {
        // Arrange
        int nonExistentProjectId = 999999;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + nonExistentProjectId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        // Act
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        // Assert
        assertEquals(404, response.statusCode(), "Should return HTTP 404 Not Found for non-existent project");
    }

    // Helper Methods

    private String createProjectRequestJson(String projectName) {
        return String.format("""
            {
                "name": "%s",
                "productName": "Integration Test Project",
                "comments": "Created by integration test",
                "rampTime": "60s",
                "simulationTime": "300s",
                "userIntervalIncrement": 1,
                "location": "unspecified",
                "stopBehavior": "END_OF_SCRIPT_GROUP",
                "workloadType": "increasing",
                "terminationPolicy": "script",
                "variables": {
                    "testVar1": "value1",
                    "testVar2": "value2"
                },
                "jobRegions": [
                    {
                        "region": "US_WEST_2",
                        "users": "10",
                        "percentage": "100"
                    }
                ]
            }
            """, projectName);
    }

    private String createComplexProjectRequestJson(String projectName) {
        return String.format("""
            {
                 "name": "%s",
                 "productName": "Complex Integration Test Product",
                 "comments": "Complex project created by integration test with test plans and script groups",
                 "rampTime": "120s",
                 "simulationTime": "600s",
                 "userIntervalIncrement": 2,
                 "location": "unspecified",
                 "stopBehavior": "END_OF_SCRIPT_GROUP",
                 "workloadType": "increasing",
                 "terminationPolicy": "script",
                 "variables": {
                     "environment": "qa",
                     "baseUrl": "https://api.example.com",
                     "timeout": "30000"
                 },
                 "jobRegions": [
                     {
                         "region": "US_WEST_2",
                         "users": "20",
                         "percentage": "60"
                     },
                     {
                         "region": "US_EAST_2",
                         "users": "15",
                         "percentage": "40"
                     }
                 ],
                 "testPlans": [
                     {
                         "name": "Main Test Plan",
                         "userPercentage": 80,
                         "scriptGroups": [
                             {
                                 "name": "Login Flow",
                                 "loop": 1,
                                 "scripts": [
                                     {
                                         "scriptId": 1,
                                         "loop": 1
                                     }
                                 ]
                             }
                         ]
                     },
                     {
                         "name": "Secondary Test Plan",
                         "userPercentage": 20,
                         "scriptGroups": [
                             {
                                 "name": "API Tests",
                                 "loop": 2,
                                 "scripts": [
                                     {
                                         "scriptId": 1,
                                         "loop": 1
                                     }
                                 ]
                             }
                         ]
                     }
                 ]
             }
            """, projectName);
    }

    private String createProjectUpdateRequestJson(String projectName) {
        return String.format("""
            {
                "name": "%s",
                "productName": "Updated Integration Test Product",
                "comments": "Updated by integration test",
                "rampTime": "90s",
                "simulationTime": "450s",
                "userIntervalIncrement": 2,
                "location": "unspecified",
                "stopBehavior": "END_OF_SCRIPT_GROUP",
                "workloadType": "increasing",
                "terminationPolicy": "script",
                "variables": {
                    "testVar1": "updatedValue1",
                    "testVar3": "newValue3"
                },
                "jobRegions": [
                    {
                        "region": "US_WEST_2",
                        "users": "15",
                        "percentage": "100"
                    }
                ]
            }
            """, projectName);
    }

    private int createTestProject(String projectName) throws Exception {
        String requestBody = createProjectRequestJson(projectName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT))
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Should create project successfully");

        Map<String, String> responseBody = objectMapper.readValue(response.body(), Map.class);
        int projectId = Integer.parseInt(responseBody.get("ProjectId"));
        createdProjectIds.add(projectId);

        return projectId;
    }

    private void verifyProjectExists(int projectId, String expectedName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Project should exist");

        JsonNode project = objectMapper.readTree(response.body());
        assertEquals(projectId, project.get("id").asInt(), "Should return correct project ID");
        assertEquals(expectedName, project.get("name").asText(), "Should return correct project name");
    }

    private void verifyComplexProjectConfiguration(int projectId, String expectedName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Project should exist");

        JsonNode project = objectMapper.readTree(response.body());
        assertEquals(projectId, project.get("id").asInt(), "Should return correct project ID");
        assertEquals(expectedName, project.get("name").asText(), "Should return correct project name");
        assertEquals("Complex Integration Test Product", project.get("productName").asText(),
                    "Should have correct product name");

        // Verify workload configuration
        if (project.has("workloads") && project.get("workloads").isArray() && project.get("workloads").size() > 0) {
            JsonNode workload = project.get("workloads").get(0);
            assertTrue(workload.has("jobConfiguration"), "Workload should have job configuration");

            JsonNode jobConfig = workload.get("jobConfiguration");
            assertEquals("120s", jobConfig.get("rampTimeExpression").asText(), "Should have correct ramp time");
            assertEquals("600s", jobConfig.get("simulationTimeExpression").asText(), "Should have correct simulation time");
        }
    }

    private void verifyProjectDeleted(int projectId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Project should be deleted and return 404");
    }

    private void deleteProject(int projectId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QA_BASE_URL + PROJECTS_ENDPOINT + "/" + projectId))
                .header(AUTHORIZATION_HEADER, API_TOKEN_HEADER)
                .timeout(Duration.ofSeconds(30))
                .DELETE()
                .build();

        httpClient.send(request, BodyHandlers.ofString());
        // Don't assert status code here since this is cleanup - project might already be deleted
    }

    // XML Validation Helper Methods

    private Document parseXmlDocument(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlContent)));
    }

    private void validateWorkloadStructure(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Validate client-class element
        String clientClass = (String) xpath.evaluate("//client-class/text()", doc, XPathConstants.STRING);
        assertNotNull(clientClass, "Should have client-class element");
        assertTrue(clientClass.contains("TankHttpClient"), "Client class should be a Tank HTTP client");

        // Validate plan element exists
        NodeList planNodes = (NodeList) xpath.evaluate("//plan", doc, XPathConstants.NODESET);
        assertTrue(planNodes.getLength() > 0, "Should have at least one test plan");

        // Validate plan attributes
        Element planElement = (Element) planNodes.item(0);
        assertTrue(planElement.hasAttribute("testPlanName"), "Plan should have testPlanName attribute");
        assertTrue(planElement.hasAttribute("userPercentage"), "Plan should have userPercentage attribute");

        String userPercentage = planElement.getAttribute("userPercentage");
        assertTrue(Pattern.matches("\\d+", userPercentage), "User percentage should be numeric");
        int percentage = Integer.parseInt(userPercentage);
        assertTrue(percentage > 0 && percentage <= 100, "User percentage should be between 1-100");
    }

    private void validateVariablesSection(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Validate variables section exists
        NodeList variablesNodes = (NodeList) xpath.evaluate("//variables", doc, XPathConstants.NODESET);
        assertTrue(variablesNodes.getLength() > 0, "Should have variables section");

        // Validate allowOverride attribute
        Element variablesElement = (Element) variablesNodes.item(0);
        assertTrue(variablesElement.hasAttribute("allowOverride"), "Variables should have allowOverride attribute");

        // Validate individual variable entries
        NodeList variableEntries = (NodeList) xpath.evaluate("//variables/variables/variables", doc, XPathConstants.NODESET);
        assertTrue(variableEntries.getLength() > 0, "Should have at least one variable defined");

        // Validate known variables from the Simple Endurance Project
        Map<String, String> expectedVariables = Map.of(
            "env", "qa",
            "taxYear", "2023",
            "Usecase", "TEST",
            "SKU", "DELUXE"
        );

        for (Map.Entry<String, String> expectedVar : expectedVariables.entrySet()) {
            String varValue = (String) xpath.evaluate(
                String.format("//variables/variables/variables[@name='%s']/@value", expectedVar.getKey()),
                doc, XPathConstants.STRING);
            assertEquals(expectedVar.getValue(), varValue,
                String.format("Variable '%s' should have correct value", expectedVar.getKey()));
        }
    }

    private void validateTestPlanStructure(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Validate test suite structure
        NodeList testSuiteNodes = (NodeList) xpath.evaluate("//testSuite", doc, XPathConstants.NODESET);
        assertTrue(testSuiteNodes.getLength() > 0, "Should have at least one test suite");

        Element testSuite = (Element) testSuiteNodes.item(0);
        assertTrue(testSuite.hasAttribute("name"), "Test suite should have name attribute");
        assertTrue(testSuite.hasAttribute("loop"), "Test suite should have loop attribute");

        // Validate test group structure
        NodeList testGroupNodes = (NodeList) xpath.evaluate("//testGroup", doc, XPathConstants.NODESET);
        assertTrue(testGroupNodes.getLength() > 0, "Should have at least one test group");

        Element testGroup = (Element) testGroupNodes.item(0);
        assertTrue(testGroup.hasAttribute("name"), "Test group should have name attribute");
        assertTrue(testGroup.hasAttribute("loop"), "Test group should have loop attribute");

        // Validate use case and test steps
        NodeList useCaseNodes = (NodeList) xpath.evaluate("//useCase", doc, XPathConstants.NODESET);
        assertTrue(useCaseNodes.getLength() > 0, "Should have at least one use case");

        NodeList testStepNodes = (NodeList) xpath.evaluate("//testStep", doc, XPathConstants.NODESET);
        assertTrue(testStepNodes.getLength() > 0, "Should have at least one test step");

        // Validate test step structure for Simple Endurance Project (think time step)
        Element testStep = (Element) testStepNodes.item(0);
        assertTrue(testStep.hasAttribute("minTime"), "Think time step should have minTime attribute");
        assertTrue(testStep.hasAttribute("maxTime"), "Think time step should have maxTime attribute");

        String minTime = testStep.getAttribute("minTime");
        String maxTime = testStep.getAttribute("maxTime");
        assertTrue(Pattern.matches("\\d+", minTime), "minTime should be numeric");
        assertTrue(Pattern.matches("\\d+", maxTime), "maxTime should be numeric");
        assertTrue(Integer.parseInt(maxTime) >= Integer.parseInt(minTime),
                  "maxTime should be greater than or equal to minTime");
    }

    private void validateClientConfiguration(Document doc) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Validate client class is properly configured
        String clientClass = (String) xpath.evaluate("//client-class/text()", doc, XPathConstants.STRING);
        assertTrue(clientClass.equals("com.intuit.tank.httpclient4.TankHttpClient4") ||
                  clientClass.equals("com.intuit.tank.httpclient5.TankHttpClient5"),
                  "Should use a valid Tank HTTP client implementation");

        // Validate XML structure integrity
        Element root = doc.getDocumentElement();
        assertNotNull(root.getAttribute("name"), "Root workload element should have name attribute");
        assertNotNull(root.getAttribute("description"), "Root workload element should have description attribute");

        // Validate namespace declaration
        String namespace = root.getNamespaceURI();
        assertEquals("urn:com/intuit/tank/harness/data/v1", namespace,
                    "Should use correct Tank harness namespace");
    }
}