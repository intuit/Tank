package com.intuit.tank.rest.mvc.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.jobs.*;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JobClientTest {
    MockWebServer mockWebServer;

    JobClient jobClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String serviceUrl = mockWebServer.url("/").toString();
        jobClient = new JobClient(serviceUrl, null, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testJobClientDefaultConstructor_SetsFieldsCorrectly()  {
        String serviceUrl = "http://localhost:8080";
        JobClient jobClient = new JobClient(serviceUrl);
        assertEquals("/v2/jobs", jobClient.getServiceBaseUrl());
    }

    @Test
    void testGetAllJobs_WhenSuccess_ReturnsJobContainer() throws JsonProcessingException {
        JobTO jobTO = new JobTO();
        jobTO.setId(33);
        JobContainer expectedContainer = new JobContainer(List.of(jobTO));
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedContainer))
                        .addHeader("Content-Type", "application/json")
        );

        JobContainer response = jobClient.getAllJobs();

        assertEquals(expectedContainer.getJobs().get(0).getId(), response.getJobs().get(0).getId());
    }

    @Test
    void testGetAllJobs_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        String serviceUrl = mockWebServer.url("/").toString();
        JobClient jobClient = new JobClient(serviceUrl);

        Exception exception = assertThrows(
                ClientException.class,
                jobClient::getAllJobs
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetJob_WhenSuccess_ReturnsJobTO() throws JsonProcessingException {
        Integer jobId = 1;
        JobTO expectedResponse = new JobTO();
        expectedResponse.setId(44);
        expectedResponse.setName("testJob");
        expectedResponse.setRampTimeMilis(1000L);
        expectedResponse.setSimulationTimeMilis(60000L);

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );

        JobTO response = jobClient.getJob(jobId);

        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getRampTimeMilis(), response.getRampTimeMilis());
        assertEquals(expectedResponse.getSimulationTimeMilis(), response.getSimulationTimeMilis());
    }

    @Test
    void testGetJob_WhenServerError_ThrowsClientException() {
        Integer jobId = 43;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class,
                () -> jobClient.getJob(jobId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }



    @Test
    void testGetJobsByProject_WhenSuccess_ReturnsJobContainer() throws JsonProcessingException {
        Integer projectId = 33;
        JobTO jobTO = new JobTO();
        jobTO.setId(projectId);
        List<JobTO> jobs = List.of(jobTO);
        JobContainer expectedContainer = new JobContainer(jobs);
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedContainer))
                        .addHeader("Content-Type", "application/json")
        );
        JobContainer response = jobClient.getJobsByProject(projectId);

        assertEquals(expectedContainer.getJobs().get(0).getId(), response.getJobs().get(0).getId());
    }

    @Test
    void testGetJobsByProject_WhenServerError_ThrowsClientException() {
        Integer projectId = 34;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(ClientException.class, () -> jobClient.getJobsByProject(projectId));
        assertTrue(exception.getMessage().contains(errorBody));
    }


    @Test
    void testCreateJob_WhenSuccess_ReturnsMap() throws JsonProcessingException {
        CreateJobRequest request = new CreateJobRequest();
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("jobId", "14");
        expectedResponse.put("status", "created");

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedResponse))
                        .addHeader("Content-Type", "application/json")
        );
        Map<String, String> response = jobClient.createJob(request);

        assertEquals(expectedResponse, response);
    }

    @Test
    void testCreateJob_WhenServerError_ThrowsClientException() {
        CreateJobRequest request = new CreateJobRequest();
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> jobClient.createJob(request)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetAllJobStatus_WhenSuccess_ReturnsList() throws JsonProcessingException {
        List<Map<String, String>> expectedList = new ArrayList<>();
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("jobId", "1");
        expectedMap.put("status", "created");
        expectedList.add(expectedMap);

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedList))
                        .addHeader("Content-Type", "application/json")
        );
        List<Map<String, String>> response = jobClient.getAllJobStatus();

        assertEquals(expectedList, response);
    }

    @Test
    void testGetAllJobStatus_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        mockWebServer.enqueue(
                new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                jobClient::getAllJobStatus
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetJobStatus_WhenSuccess_ReturnsJobStatus() {
        Integer jobId = 2;
        String expectedResponse = "Running";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain")
        );
        String response = jobClient.getJobStatus(jobId);

        assertEquals(expectedResponse, response);
    }

    @Test
    void testGetJobStatus_WhenServerError_ThrowsClientException() {
        Integer jobId = 3;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> jobClient.getJobStatus(jobId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetJobVMStatuses_WhenSuccess_ReturnsJobStatuses() throws JsonProcessingException{
        String jobId = "234";
        CloudVmStatus expectedCloudVmStatus = new CloudVmStatus(
                "testInstanceId",
                "234",
                "testSecurityGroup",
                JobStatus.Running,
                VMImageType.AGENT,
                VMRegion.US_WEST_2,
                VMStatus.running,
                new ValidationStatus(),
                100,
                50,
                new Date(),
                new Date()
        );

        CloudVmStatusContainer cloudVmStatusContainer = new CloudVmStatusContainer(Set.of(expectedCloudVmStatus));

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(cloudVmStatusContainer))
                        .addHeader("Content-Type", "application/json")
        );

        CloudVmStatusContainer response = jobClient.getJobVMStatuses(jobId);

        CloudVmStatus actualCloudVmStatus = response.getStatuses().iterator().next();

        assertEquals(jobId, actualCloudVmStatus.getJobId());
        assertEquals(expectedCloudVmStatus, actualCloudVmStatus);
    }

    @Test
    void testGetJobVMStatuses_WhenServerError_ThrowsClientException() {
        String jobId = "243";
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> jobClient.getJobVMStatuses(jobId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    // Job Actions

    public enum JobActions {
        START,
        STOP,
        PAUSE,
        RESUME,
        KILL
    }

    @ParameterizedTest
    @EnumSource(JobActions.class)
    void jobOperation_WhenSuccess_ReturnsString(JobActions operation) {
        Integer jobId = 23;
        String expectedResponse = "JobStatus";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain"));

        String response = performOperation(operation, jobId);

        assertEquals(expectedResponse, response);
    }

    @ParameterizedTest
    @EnumSource(JobActions.class)
    void jobOperation_WhenServerError_ThrowsClientException(JobActions operation) {
        Integer jobId = 24;
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> performOperation(operation, jobId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    private String performOperation(JobActions operation, Integer jobId) {
        switch (operation) {
            case START:
                return jobClient.startJob(jobId);
            case STOP:
                return jobClient.stopJob(jobId);
            case PAUSE:
                return jobClient.pauseJob(jobId);
            case RESUME:
                return jobClient.resumeJob(jobId);
            case KILL:
                return jobClient.killJob(jobId);
            default:
                throw new IllegalArgumentException("Invalid Job Action: " + operation);
        }
    }
}
