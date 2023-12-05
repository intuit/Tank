package com.intuit.tank.rest.mvc.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.projects.*;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectClientTest {
    MockWebServer mockWebServer;

    ProjectClient projectClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String serviceUrl = mockWebServer.url("/").toString();
        projectClient = new ProjectClient(serviceUrl, null, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testProjectClientDefaultConstructor_SetsFieldsCorrectly()  {
        String serviceUrl = "http://localhost:8080";
        ProjectClient projectClient = new ProjectClient(serviceUrl);

        assertEquals("/v2/projects", projectClient.getServiceBaseUrl());
    }

    @Test
    void testGetProjects_WhenSuccess_ReturnsProjectContainer() throws JsonProcessingException {
        ProjectTO projectTO = new ProjectTO();
        projectTO.setId(4);
        projectTO.setName("testName");
        projectTO.setRampTime("1h");
        projectTO.setSimulationTime(60000L);
        projectTO.setTerminationPolicy(TerminationPolicy.script);
        projectTO.setWorkloadType(IncrementStrategy.increasing);
        ProjectContainer expectedResponse = new ProjectContainer(List.of(projectTO));
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );
        ProjectContainer response = projectClient.getProjects();
        ProjectTO expectedProjectTO = expectedResponse.getProjects().get(0);

        assertEquals(expectedProjectTO.getId(), projectTO.getId());
        assertEquals(expectedProjectTO.getName(), projectTO.getName());
        assertEquals(expectedProjectTO.getRampTime(), projectTO.getRampTime());
        assertEquals(expectedProjectTO.getSimulationTime(), projectTO.getSimulationTime());
        assertEquals(expectedProjectTO.getTerminationPolicy(), projectTO.getTerminationPolicy());
        assertEquals(expectedProjectTO.getWorkloadType(), projectTO.getWorkloadType());
    }

    @Test
    void testGetProjects_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));
        Exception exception =  assertThrows(
                ClientException.class,
                () -> projectClient.getProjects()
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetProjectNames_WhenSuccess_ReturnsProjectNames() throws JsonProcessingException {
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("1", "TestProject1");
        expectedResponse.put("2", "TestProject2");

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        Map<Integer, String> response = projectClient.getProjectNames();

        Map<String, String> actualResponse = response.entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), Map.Entry::getValue));

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testGetProjectNames_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () -> projectClient.getProjectNames());
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetProject_WhenSuccess_ReturnsProjectTO() throws JsonProcessingException {
        Integer projectId = 1;
        ProjectTO expectedProjectTO = new ProjectTO();
        expectedProjectTO.setId(4);
        expectedProjectTO.setName("testName");
        expectedProjectTO.setRampTime("1h");
        expectedProjectTO.setSimulationTime(60000L);
        expectedProjectTO.setTerminationPolicy(TerminationPolicy.script);
        expectedProjectTO.setWorkloadType(IncrementStrategy.increasing);

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedProjectTO))
                        .addHeader("Content-Type", "application/json")
        );

        ProjectTO actualProjectTO = projectClient.getProject(projectId);

        assertEquals(expectedProjectTO.getId(), actualProjectTO.getId());
        assertEquals(expectedProjectTO.getName(), actualProjectTO.getName());
        assertEquals(expectedProjectTO.getRampTime(), actualProjectTO.getRampTime());
        assertEquals(expectedProjectTO.getSimulationTime(), actualProjectTO.getSimulationTime());
        assertEquals(expectedProjectTO.getTerminationPolicy(), actualProjectTO.getTerminationPolicy());
        assertEquals(expectedProjectTO.getWorkloadType(), actualProjectTO.getWorkloadType());
    }

    @Test
    void testGetProject_WhenServerError_ThrowsClientException() {
        Integer projectId = 1;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> projectClient.getProject(projectId));
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testCreateProject_WhenSuccess_ReturnsMap() throws JsonProcessingException {
        AutomationRequest request = createAutomationRequest();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("ProjectId", "17");
        expectedResponse.put("status", "Created");

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        Map<String, String> response = projectClient.createProject(request);

        assertEquals(expectedResponse, response);
    }

    @Test
    void testCreateProject_WhenServerError_ThrowsClientException() {
        AutomationRequest request = createAutomationRequest();
        String errorBody = "Internal Server Error";
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () -> projectClient.createProject(request));
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testUpdateProject_WhenSuccess_ReturnsMap() throws JsonProcessingException {
        AutomationRequest request = createAutomationRequest();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("ProjectId", "18");
        expectedResponse.put("status", "Created");

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        Map<String, String> response = projectClient.updateProject(request);

        assertEquals(expectedResponse, response);
    }

    @Test
    void testUpdateProject_WhenServerError_ThrowsClientException() {
        AutomationRequest request = createAutomationRequest();
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () -> projectClient.updateProject(request));
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDownloadTestScriptForProject_WhenSuccess_ReturnsContentAsString() {
        Integer projectId = 7;
        String expectedResponse = "Script Content";

        mockWebServer.enqueue(new MockResponse()
                .setBody(expectedResponse)
                .addHeader("Content-Type", "text/plain"));

        String actualResponse = projectClient.downloadTestScriptForProject(projectId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testDownloadTestScriptForProject_WhenServerError_ThrowsClientException() {
        Integer projectId = 9;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () ->
                projectClient.downloadTestScriptForProject(projectId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testDeleteProject_WhenSuccess_ReturnsString() {
        Integer projectId = 11;
        String expectedResponse = "Project deleted successfully";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain")
        );

        String response = projectClient.deleteProject(projectId);
        System.out.println(response);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testDeleteProject_WhenServerError_ThrowsClientException() {
        Integer projectId = 12;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> projectClient.deleteProject(projectId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    private AutomationRequest createAutomationRequest() {
        return AutomationRequest.builder()
                .withProductName("Test Product")
                .withRampTime("1920s")
                .withSimulationTime("7200s")
                .withUserIntervalIncrement(1)
                .withAddedTestPlan(new AutomationTestPlan("Main",100,0,
                        new ArrayList<>(List.of(new AutomationScriptGroup("testScriptGroup", 0, 0,
                                new ArrayList<>(List.of(new AutomationScriptGroupStep(123, "testScript", 1, 0))))))))
                .build();
    }
}
