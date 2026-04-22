package com.intuit.tank.rest.mvc.rest.services.agent;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import com.intuit.tank.agent.models.TankHttpClientDefinitionContainer;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AgentServiceV2ImplTest {

    @InjectMocks
    private AgentServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private MockedStatic<AWSXRay> xrayMock;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        xrayMock = Mockito.mockStatic(AWSXRay.class);
        Segment mockSegment = mock(Segment.class);
        Subsegment mockSubsegment = mock(Subsegment.class);
        xrayMock.when(AWSXRay::getCurrentSegment).thenReturn(mockSegment);
        xrayMock.when(() -> AWSXRay.beginSubsegment(anyString())).thenReturn(mockSubsegment);
    }

    @AfterEach
    void tearDown() throws Exception {
        xrayMock.close();
        mocks.close();
    }

    // =====================================================================
    // ping
    // =====================================================================

    @Test
    void ping_returnsPong() {
        String result = service.ping();
        assertTrue(result.startsWith("PONG"));
        assertTrue(result.contains("AgentServiceV2"));
    }

    // =====================================================================
    // getSettings
    // =====================================================================

    @Test
    void getSettings_readsAgentSettingsFile() {
        File mockConfigFile = mock(File.class);
        File mockParentDir = mock(File.class);

        when(mockConfigFile.getParentFile()).thenReturn(mockParentDir);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getSourceConfigFile()).thenReturn(mockConfigFile))) {
            // When agent-settings.xml doesn't exist, it falls back to config file
            // This will throw because we can't read a mock file — testing exception path
            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getSettings());
        }
    }

    // =====================================================================
    // getHeaders
    // =====================================================================

    @Test
    void getHeaders_returnsConfiguredHeaders() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "text/xml");

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getRequestHeaderMap()).thenReturn(headerMap);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getAgentConfig()).thenReturn(mockAgentConfig))) {

            Headers result = service.getHeaders();

            assertNotNull(result);
            assertEquals(2, result.getHeaders().size());
            assertEquals("Accept", result.getHeaders().get(0).getKey());
            assertEquals("application/json", result.getHeaders().get(0).getValue());
        }
    }

    @Test
    void getHeaders_returnsEmptyWhenNoHeaders() {
        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getRequestHeaderMap()).thenReturn(null);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getAgentConfig()).thenReturn(mockAgentConfig))) {

            Headers result = service.getHeaders();
            assertNotNull(result);
            assertTrue(result.getHeaders().isEmpty());
        }
    }

    @Test
    void getHeaders_throwsOnError() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getAgentConfig()).thenThrow(new RuntimeException("config error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getHeaders());
        }
    }

    // =====================================================================
    // getClients
    // =====================================================================

    @Test
    void getClients_returnsClientDefinitions() {
        Map<String, String> clientMap = new LinkedHashMap<>();
        clientMap.put("Apache", "com.intuit.tank.http.ApacheClient");

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientMap()).thenReturn(clientMap);
        when(mockAgentConfig.getTankClientDefault()).thenReturn("Apache");

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getAgentConfig()).thenReturn(mockAgentConfig))) {

            TankHttpClientDefinitionContainer result = service.getClients();

            assertNotNull(result);
            assertEquals(1, result.getDefinitions().size());
            assertEquals("Apache", result.getDefinitions().get(0).getName());
            assertEquals("Apache", result.getDefaultDefinition());
        }
    }

    @Test
    void getClients_usesFirstDefinitionWhenNoDefault() {
        Map<String, String> clientMap = new LinkedHashMap<>();
        clientMap.put("Custom", "com.example.CustomClient");

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientMap()).thenReturn(clientMap);
        when(mockAgentConfig.getTankClientDefault()).thenReturn(null);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getAgentConfig()).thenReturn(mockAgentConfig))) {

            TankHttpClientDefinitionContainer result = service.getClients();

            assertEquals("Custom", result.getDefaultDefinition());
        }
    }

    @Test
    void getClients_throwsOnError() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getAgentConfig()).thenThrow(new RuntimeException("config error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getClients());
        }
    }

    // =====================================================================
    // agentReady
    // =====================================================================

    @Test
    void agentReady_registersAgent() {
        AgentData agentData = new AgentData();
        AgentTestStartData startData = new AgentTestStartData();
        startData.setJobId("42");

        JobManager mockJobManager = mock(JobManager.class);
        when(mockJobManager.registerAgentForJob(agentData)).thenReturn(startData);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobManager.class))).thenReturn(mockJobManager))) {

            AgentTestStartData result = service.agentReady(agentData);

            assertEquals("42", result.getJobId());
        }
    }

    @Test
    void agentReady_throwsOnRegistrationError() {
        AgentData agentData = new AgentData();

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobManager.class)))
                        .thenThrow(new RuntimeException("CDI error")))) {

            assertThrows(GenericServiceBadRequestException.class, () -> service.agentReady(agentData));
        }
    }

    // =====================================================================
    // setStandaloneAgentAvailability
    // =====================================================================

    @Test
    void setStandaloneAgentAvailability_addsAvailability() {
        AgentAvailability availability = mock(AgentAvailability.class);
        StandaloneAgentTracker tracker = mock(StandaloneAgentTracker.class);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(StandaloneAgentTracker.class)))
                        .thenReturn(tracker))) {

            service.setStandaloneAgentAvailability(availability);

            verify(tracker).addAvailability(availability);
        }
    }

    @Test
    void setStandaloneAgentAvailability_throwsOnError() {
        AgentAvailability availability = mock(AgentAvailability.class);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(StandaloneAgentTracker.class)))
                        .thenThrow(new RuntimeException("CDI error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class,
                    () -> service.setStandaloneAgentAvailability(availability));
        }
    }

    // =====================================================================
    // getInstanceStatus
    // =====================================================================

    @Test
    void getInstanceStatus_returnsStatus() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Running,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.running, null, 10, 100, null, null);

        JobEventSender mockSender = mock(JobEventSender.class);
        when(mockSender.getVmStatus("i-123")).thenReturn(status);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            CloudVmStatus result = service.getInstanceStatus("i-123");

            assertNotNull(result);
            assertEquals("i-123", result.getInstanceId());
            assertEquals(JobStatus.Running, result.getJobStatus());
        }
    }

    @Test
    void getInstanceStatus_throwsOnError() {
        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("not found")))) {

            assertThrows(GenericServiceResourceNotFoundException.class,
                    () -> service.getInstanceStatus("i-999"));
        }
    }

    // =====================================================================
    // setInstanceStatus
    // =====================================================================

    @Test
    void setInstanceStatus_delegatesToJobEventSender() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Running,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.running, null, 10, 100, null, null);
        JobEventSender mockSender = mock(JobEventSender.class);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            service.setInstanceStatus("i-123", status);

            verify(mockSender).setVmStatus("i-123", status);
        }
    }

    @Test
    void setInstanceStatus_throwsOnError() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Running,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.running, null, 10, 100, null, null);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class,
                    () -> service.setInstanceStatus("i-123", status));
        }
    }

    // =====================================================================
    // stopInstance
    // =====================================================================

    @Test
    void stopInstance_stopsAndReturnsStatus() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Stopped,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.stopping, null, 0, 100, null, null);
        JobEventSender mockSender = mock(JobEventSender.class);
        when(mockSender.getVmStatus("i-123")).thenReturn(status);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            String result = service.stopInstance("i-123");

            assertEquals("stopping", result);
            verify(mockSender).stopAgent("i-123");
        }
    }

    @Test
    void stopInstance_throwsOnError() {
        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class,
                    () -> service.stopInstance("i-123"));
        }
    }

    // =====================================================================
    // pauseInstance
    // =====================================================================

    @Test
    void pauseInstance_pausesAndReturnsStatus() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Paused,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.rampPaused, null, 5, 100, null, null);
        JobEventSender mockSender = mock(JobEventSender.class);
        when(mockSender.getVmStatus("i-123")).thenReturn(status);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            String result = service.pauseInstance("i-123");

            assertEquals("rampPaused", result);
            verify(mockSender).pauseRampInstance("i-123");
        }
    }

    // =====================================================================
    // resumeInstance
    // =====================================================================

    @Test
    void resumeInstance_resumesAndReturnsStatus() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Running,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.running, null, 10, 100, null, null);
        JobEventSender mockSender = mock(JobEventSender.class);
        when(mockSender.getVmStatus("i-123")).thenReturn(status);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            String result = service.resumeInstance("i-123");

            assertEquals("running", result);
            verify(mockSender).resumeRampInstance("i-123");
        }
    }

    // =====================================================================
    // killInstance
    // =====================================================================

    @Test
    void killInstance_killsAndReturnsStatus() {
        CloudVmStatus status = new CloudVmStatus("i-123", "42", "sg", JobStatus.Completed,
                VMImageType.AGENT, VMRegion.US_WEST_2, VMStatus.terminated, null, 0, 100, null, null);
        JobEventSender mockSender = mock(JobEventSender.class);
        when(mockSender.getVmStatus("i-123")).thenReturn(status);

        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenReturn(mockSender))) {

            String result = service.killInstance("i-123");

            assertEquals("terminated", result);
            verify(mockSender).killInstance("i-123");
        }
    }

    @Test
    void killInstance_throwsOnError() {
        try (@SuppressWarnings({"rawtypes", "unchecked"}) MockedConstruction<ServletInjector> ignored = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(JobEventSender.class)))
                        .thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceCreateOrUpdateException.class,
                    () -> service.killInstance("i-123"));
        }
    }
}
