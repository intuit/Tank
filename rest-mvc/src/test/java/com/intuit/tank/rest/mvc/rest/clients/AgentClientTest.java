package com.intuit.tank.rest.mvc.rest.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.rest.mvc.rest.clients.util.ClientException;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.vm.agent.messages.*;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
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
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AgentClientTest {

    MockWebServer mockWebServer;

    AgentClient agentClient;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String serviceUrl = mockWebServer.url("/").toString();
        agentClient = new AgentClient(serviceUrl, null, null);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testAgentClientDefaultConstructor_SetsFieldsCorrectly()  {
        String serviceUrl = "http://localhost:8080";
        AgentClient agentClient = new AgentClient(serviceUrl);
        assertEquals("/v2/agent", agentClient.getServiceBaseUrl());
    }

    @Test
    void testGetSettings_WhenSuccess_ReturnsSettings() throws InterruptedException {
        String anySettings = "<settings>Any Settings</settings>";

        mockWebServer.enqueue(new MockResponse().setBody(anySettings).addHeader("Content-Type", "application/xml"));
        String response = agentClient.getSettings();
        assertEquals(anySettings, response);
        assertEquals("GET /v2/agent/settings HTTP/1.1", mockWebServer.takeRequest().getRequestLine());
    }

    @Test
    void testGetSettings_WhenError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));
        Exception exception = assertThrows(ClientException.class, agentClient::getSettings);
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetSupportFiles_WhenSuccess_ReturnsDataBuffer() {
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        DataBuffer expectedDataBuffer = dataBufferFactory.wrap("Any support files".getBytes());
        mockWebServer.enqueue(new MockResponse().setBody("Any support files").setHeader("Content-Type", "application/octet-stream"));


        Mono<DataBuffer> responseMono = agentClient.getSupportFiles();

        StepVerifier.create(responseMono)
                .assertNext(dataBuffer -> {
                    assertEquals(expectedDataBuffer.readableByteCount(), dataBuffer.readableByteCount());
                    assertEquals(expectedDataBuffer.asByteBuffer().compareTo(dataBuffer.asByteBuffer()), 0);
                })
                .verifyComplete();
    }

    @Test
    void testGetSupportFiles_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));


        Mono<DataBuffer> responseMono = agentClient.getSupportFiles();

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof ClientException
                        && throwable.getMessage().contains(errorBody))
                .verify();
    }

    @Test
    void testAgentReady_WhenSuccess_ReturnsAgentTestStartData() throws JsonProcessingException {
        AgentData givenAgentData = new AgentData("234", "testInstanceId", "testInstanceUrl",
                                            1, VMRegion.US_EAST_2, "testZone");
        AgentTestStartData expectedTestStartData = new AgentTestStartData();
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(expectedTestStartData))
                        .setHeader("Content-Type", "application/json")
        );


        AgentTestStartData response = agentClient.agentReady(givenAgentData);

        assertEquals(expectedTestStartData.getJobId(), response.getJobId());
        assertEquals(expectedTestStartData.getRampTime(), response.getRampTime());
        assertEquals(expectedTestStartData.getSimulationTime(), response.getSimulationTime());
        assertEquals(expectedTestStartData.getStartUsers(), response.getStartUsers());
        assertEquals(expectedTestStartData.getConcurrentUsers(), response.getConcurrentUsers());
        assertEquals(expectedTestStartData.getIncrementStrategy(), response.getIncrementStrategy());
        assertEquals(expectedTestStartData.getUserIntervalIncrement(), response.getUserIntervalIncrement());
        assertEquals(expectedTestStartData.getDataFiles(), response.getDataFiles());
        assertEquals(expectedTestStartData.getAgentInstanceNum(), response.getAgentInstanceNum());
    }

    @Test
    void testAgentReady_WhenServerError_ThrowsClientException() {
        AgentData givenAgentData = new AgentData();
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> agentClient.agentReady(givenAgentData)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetHeaders_WhenSuccess_ReturnsHeaders() throws JsonProcessingException {
        String validHeadersXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                    "<headers xmlns=\"urn:wats/domain/agent/v1\"><headers><header>" +
                                        "<key>testKey</key><value>testValue</value>" +
                                            "</header></headers></headers>";
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(validHeadersXml) // valid XML that corresponds to Headers object
                        .addHeader("Content-Type", "application/xml")
        );
        Headers expectedHeaders = new Headers(List.of(new Header("testKey", "testValue")));

        Headers response = agentClient.getHeaders();

        assertEquals(expectedHeaders.getHeaders().get(0).getKey(), response.getHeaders().get(0).getKey());
        assertEquals(expectedHeaders.getHeaders().get(0).getValue(), response.getHeaders().get(0).getValue());
    }

    @Test
    void testGetHeaders_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                agentClient::getHeaders
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetClients_WhenSuccess_ReturnsTankHttpClientDefinitionContainer() throws JsonProcessingException {
        String validHeadersXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<TankHttpClientDefinitionContainer xmlns=\"urn:tank/domain/agent/v1\">" +
                "<definitions><tankHttpClientDefinition><name>TestClient</name>" +
                "<className>com.intuit.tank.test.client</className>" +
                "</tankHttpClientDefinition>" +
                "</definitions><defaultDefinition>TestClient</defaultDefinition></TankHttpClientDefinitionContainer>";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(validHeadersXml)
                        .addHeader("Content-Type", "application/xml")
        );


        TankHttpClientDefinitionContainer response = agentClient.getClients();

        assertEquals("com.intuit.tank.test.client", response.getDefinitions().get(0).getClassName());
        assertEquals("TestClient", response.getDefinitions().get(0).getName());
        assertEquals("TestClient", response.getDefaultDefinition());
    }

    @Test
    void testGetClients_WhenServerError_ThrowsClientException() {
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                agentClient::getClients
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testSetStandaloneAgentAvailability_WhenSuccess_ReturnsNull() {
        AgentAvailability givenAgentAvailability = new AgentAvailability();
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody("")
                        .addHeader("Content-Type", "application/json")
        );


        Void response = agentClient.setStandaloneAgentAvailability(givenAgentAvailability);
        assertNull(response);
    }

    @Test
    void testSetStandaloneAgentAvailability_WhenServerError_ThrowsClientException() {
        AgentAvailability givenAgentAvailability = new AgentAvailability();
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> agentClient.setStandaloneAgentAvailability(givenAgentAvailability)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testGetInstanceStatus_WhenSuccess_ReturnsCloudVmStatus() throws JsonProcessingException {
        CloudVmStatus cloudVmStatus = new CloudVmStatus(
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

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(new ObjectMapper().writeValueAsString(cloudVmStatus))
                        .addHeader("Content-Type", "application/json")
        );


        CloudVmStatus response = agentClient.getInstanceStatus("testInstanceId");

        assertEquals(cloudVmStatus, response);
    }

    @Test
    void getInstanceStatus_WhenServerError_ThrowsClientException() {
        String givenInstanceId = "instanceId";
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> agentClient.getInstanceStatus(givenInstanceId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    @Test
    void testSetInstanceStatus_WhenSuccess_ReturnsNull() {
        CloudVmStatus cloudVmStatus = new CloudVmStatus(
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

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody("")
                        .addHeader("Content-Type", "application/json")
        );


        Void response = agentClient.setInstanceStatus("testInstanceId", cloudVmStatus);

        assertNull(response);
    }

    @Test
    void setInstanceStatus_WhenServerError_ThrowsClientException() {
        CloudVmStatus cloudVmStatus = new CloudVmStatus(
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
        String errorBody = "Internal Server Error";
        int errorCode = 500;
        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(errorCode));

        Exception exception = assertThrows(
                ClientException.class,
                () -> agentClient.setInstanceStatus("testInstanceId", cloudVmStatus)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    // Instance Actions

    public enum InstanceActions {
        STOP,
        PAUSE,
        RESUME,
        KILL
    }

    @ParameterizedTest
    @EnumSource(InstanceActions.class)
    void testInstanceActions_WhenSuccess_ReturnsString(InstanceActions operation) {
        String instanceId = "testInstanceId";
        String expectedResponse = "InstanceStatus";

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(expectedResponse)
                        .addHeader("Content-Type", "text/plain"));

        String response = performOperation(operation, instanceId);

        assertEquals(expectedResponse, response);
    }

    @ParameterizedTest
    @EnumSource(InstanceActions.class)
    void testInstanceActions_WhenServerError_ThrowsClientException(InstanceActions operation) {
        String instanceId = "testInstanceId";
        String errorBody = "Internal Server Error";

        mockWebServer.enqueue(new MockResponse().setBody(errorBody).setResponseCode(500));

        Exception exception = assertThrows(
                ClientException.class,
                () -> performOperation(operation, instanceId)
        );
        assertTrue(exception.getMessage().contains(errorBody));
    }

    private String performOperation(InstanceActions operation,  String instanceId) {
        switch (operation) {
            case STOP:
                return agentClient.stopInstance(instanceId);
            case PAUSE:
                return agentClient.pauseInstance(instanceId);
            case RESUME:
                return agentClient.resumeInstance(instanceId);
            case KILL:
                return agentClient.killInstance(instanceId);
            default:
                throw new IllegalArgumentException("Invalid Instance Action: " + operation);
        }
    }
}
