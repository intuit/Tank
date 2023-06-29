package com.intuit.tank.rest.mvc.rest.controllers.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;


import javax.servlet.ServletContext;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.services.agent.AgentServiceV2Impl;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.agent.messages.Header;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mockStatic;
import org.springframework.beans.factory.annotation.Autowired;


public class AgentServiceV2ImplTest {

    @InjectMocks
    private AgentServiceV2Impl agentServiceV2;

    @Mock
    private TankConfig tankConfig;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPing() {
        assertEquals("PONG AgentServiceV2", agentServiceV2.ping());
    }

    @Test
    public void testGetSettings() {
        TankConfig mockTankConfig = mock(TankConfig.class);
        File mockConfigFile = mock(File.class);
        File mockParentFile = mock(File.class);
        File mockAgentConfigFile = mock(File.class);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected TankConfig createTankConfig() {
                return mockTankConfig;
            }

            @Override
            protected String readFileToString(File file) throws IOException {
                return "mockedSettings";
            }

            @Override
            protected File createAgentConfigFile(File parentFile) {
                return mockAgentConfigFile;
            }
        };

        when(mockTankConfig.getSourceConfigFile()).thenReturn(mockConfigFile);
        when(mockConfigFile.getParentFile()).thenReturn(mockParentFile);
        when(mockAgentConfigFile.exists()).thenReturn(true);
        when(mockAgentConfigFile.isFile()).thenReturn(true);

        String settings = agentService.getSettings();
        assertEquals("mockedSettings", settings);
    }

    @Test
    public void testGetSettingsException() throws Exception {
        File mockedConfigFile = mock(File.class);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected String readFileToString(File file) throws IOException {
                throw new IOException("trigger exception");
            }
        };

        when(mockedConfigFile.getParentFile()).thenReturn(mockedConfigFile);
        when(mockedConfigFile.exists()).thenReturn(true);
        when(mockedConfigFile.isFile()).thenReturn(true);

        Exception exception = assertThrows(GenericServiceResourceNotFoundException.class, agentService::getSettings);
        assertTrue(exception.getMessage().contains("settings file"));
    }

    @Test
    public void testGetSupportFiles() throws Exception {
        FileStorage mockFileStorage = mock(FileStorage.class);
        FileData mockFileData = mock(FileData.class);
        List<FileData> mockFileDataList = Collections.singletonList(mockFileData);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected TankConfig createTankConfig() {
                TankConfig mockTankConfig = mock(TankConfig.class);
                when(mockTankConfig.getTmpDir()).thenReturn("/tmp");
                return mockTankConfig;
            }

            @Override
            protected FileStorage createFileStorage(String jarDir, boolean isReadOnly) {
                return mockFileStorage;
            }

            @Override
            protected ZipOutputStream createZipOutputStream(FileOutputStream fileOutputStream) throws FileNotFoundException {
                return mock(ZipOutputStream.class);
            }

            @Override
            protected File createFile(String path, String filename) {
                File mockFile = mock(File.class);
                when(mockFile.exists()).thenReturn(true);
                return mockFile;
            }
        };

        when(mockFileStorage.listFileData("")).thenReturn(mockFileDataList);
        when(mockFileData.getFileName()).thenReturn("filename");
        File supportFiles = agentService.getSupportFiles();
        assertNotNull(supportFiles);
    }

    @Test
    public void testAgentReady() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }

            @Override
            protected ServletInjector<JobManager> createServletInjector() {
                ServletInjector<JobManager> mockServletInjector = mock(ServletInjector.class);
                JobManager mockJobManager = mock(JobManager.class);
                AgentTestStartData mockData = mock(AgentTestStartData.class);


                when(mockServletInjector.getManagedBean(any(), eq(JobManager.class))).thenReturn(mockJobManager);
                when(mockJobManager.registerAgentForJob(any())).thenReturn(mockData);


                return mockServletInjector;
            }
        };

        AgentData mockAgentData = mock(AgentData.class);
        AgentTestStartData result = agentService.agentReady(mockAgentData);
        assertNotNull(result);
    }

    @Test
    public void testAgentReadyException() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }

            @Override
            protected ServletInjector<JobManager> createServletInjector() {
                ServletInjector<JobManager> mockServletInjector = mock(ServletInjector.class);


                when(mockServletInjector.getManagedBean(any(), eq(JobManager.class)))
                        .thenThrow(new RuntimeException("trigger exception"));

                return mockServletInjector;
            }
        };

        AgentData mockAgentData = mock(AgentData.class);

        assertThrows(GenericServiceBadRequestException.class, () -> {
            agentService.agentReady(mockAgentData);
        });
    }

    @Test
    public void testGetHeaders() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected TankConfig createTankConfig() {
                TankConfig mockTankConfig = mock(TankConfig.class);
                AgentConfig mockAgentConfig = mock(AgentConfig.class);
                Map<String, String> requestHeaderMap = new HashMap<>();
                requestHeaderMap.put("testKey1", "testValue1");
                requestHeaderMap.put("testKey2", "testValue2");

                when(mockTankConfig.getAgentConfig()).thenReturn(mockAgentConfig);
                when(mockAgentConfig.getRequestHeaderMap()).thenReturn(requestHeaderMap);

                return mockTankConfig;
            }
        };

        Headers headers = agentService.getHeaders();

        assertNotNull(headers);
        assertEquals(2, headers.getHeaders().size());
        assertEquals("testKey2", headers.getHeaders().get(0).getKey());
        assertEquals("testValue2", headers.getHeaders().get(0).getValue());
        assertEquals("testKey1", headers.getHeaders().get(1).getKey());
        assertEquals("testValue1", headers.getHeaders().get(1).getValue());
    }

    @Test
    public void testGetHeadersException() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected TankConfig createTankConfig() {
                TankConfig mockTankConfig = mock(TankConfig.class);

                when(mockTankConfig.getAgentConfig()).thenThrow(new RuntimeException("trigger exception"));

                return mockTankConfig;
            }
        };
        assertThrows(GenericServiceResourceNotFoundException.class, agentService::getHeaders);
    }

    @Test
    public void testGetClients() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected TankConfig createTankConfig() {
                TankConfig mockTankConfig = mock(TankConfig.class);
                AgentConfig mockAgentConfig = mock(AgentConfig.class);
                Map<String, String> tankClientMap = new HashMap<>();
                tankClientMap.put("testName1", "testClass1");
                tankClientMap.put("testName2", "testClass2");

                when(mockTankConfig.getAgentConfig()).thenReturn(mockAgentConfig);
                when(mockAgentConfig.getTankClientMap()).thenReturn(tankClientMap);
                when(mockAgentConfig.getTankClientDefault()).thenReturn("testName1");

                return mockTankConfig;
            }
        };

        TankHttpClientDefinitionContainer clients = agentService.getClients();

        assertNotNull(clients);
        assertEquals(2, clients.getDefinitions().size());
        assertEquals("testName1", clients.getDefaultDefinition());
        assertEquals("testName1", clients.getDefinitions().get(0).getName());
        assertEquals("testClass1", clients.getDefinitions().get(0).getClassName());
        assertEquals("testName2", clients.getDefinitions().get(1).getName());
        assertEquals("testClass2", clients.getDefinitions().get(1).getClassName());
    }

    @Test
    public void testGetClientsException() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected TankConfig createTankConfig() {
                TankConfig mockTankConfig = mock(TankConfig.class);
                when(mockTankConfig.getAgentConfig()).thenThrow(new RuntimeException("trigger exception"));
                return mockTankConfig;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, agentService::getClients);
    }

    @Test
    public void testSetStandaloneAgentAvailability() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected StandaloneAgentTracker createStandaloneAgentTracker(ServletContext servletContext) {
                return mock(StandaloneAgentTracker.class);
            }
        };

        AgentAvailability mockAvailability = mock(AgentAvailability.class);
        assertDoesNotThrow(() -> agentService.setStandaloneAgentAvailability(mockAvailability));
    }

    @Test
    public void testSetStandaloneAgentAvailabilityException() {
        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected StandaloneAgentTracker createStandaloneAgentTracker(ServletContext servletContext) {
                StandaloneAgentTracker mockTracker = mock(StandaloneAgentTracker.class);
                doThrow(new RuntimeException("Mocked exception")).when(mockTracker).addAvailability(any());
                return mockTracker;
            }
        };

        AgentAvailability mockAvailability = mock(AgentAvailability.class);
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> agentService.setStandaloneAgentAvailability(mockAvailability));
    }

    @Test
    public void testGetInstanceStatus() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }

            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
        };

        when(mockJobEventSender.getVmStatus(instanceId)).thenReturn(mockStatus);
        CloudVmStatus result = agentService.getInstanceStatus(instanceId);

        assertEquals(mockStatus, result);
    }

    @Test
    public void testGetInstanceStatusException() {
        String instanceId = "testInstanceId";
        JobEventSender mockJobEventSender = mock(JobEventSender.class);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }

            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
        };

        when(mockJobEventSender.getVmStatus(instanceId)).thenThrow(new RuntimeException("trigger exception"));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> agentService.getInstanceStatus(instanceId));

    }

    @Test
    public void testSetInstanceStatus() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);


        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }

            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
        };

        when(mockStatus.getJobId()).thenReturn("testJobId");
        when(mockStatus.getCurrentUsers()).thenReturn(10);
        when(mockStatus.getTotalUsers()).thenReturn(20);
        when(mockStatus.getTotalTps()).thenReturn(30);

        agentService.setInstanceStatus(instanceId, mockStatus);
        verify(mockJobEventSender).setVmStatus(instanceId, mockStatus);
    }

    @Test
    public void testSetInstanceStatusException() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }

            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
        };
        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).setVmStatus(anyString(), any(CloudVmStatus.class));
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> {
            agentService.setInstanceStatus(instanceId, mockStatus);
        });
    }

    @Test
    public void testStopInstance() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        VMStatus mockVmStatus = VMStatus.stopped;
        when(mockStatus.getVmStatus()).thenReturn(mockVmStatus);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }
            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
            @Override
            public CloudVmStatus getInstanceStatus(String instanceId) {
                return mockStatus;
            }
        };
        String result = agentService.stopInstance(instanceId);
        assertEquals("stopped", result);
    }

    @Test
    public void testPauseInstance() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        VMStatus mockVmStatus = VMStatus.rampPaused;
        when(mockStatus.getVmStatus()).thenReturn(mockVmStatus);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }
            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
            @Override
            public CloudVmStatus getInstanceStatus(String instanceId) {
                return mockStatus;
            }
        };
        String result = agentService.pauseInstance(instanceId);
        assertEquals("rampPaused", result);
    }

    @Test
    public void testResumeInstance() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        VMStatus mockVmStatus = VMStatus.running;
        when(mockStatus.getVmStatus()).thenReturn(mockVmStatus);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }
            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
            @Override
            public CloudVmStatus getInstanceStatus(String instanceId) {
                return mockStatus;
            }
        };
        String result = agentService.resumeInstance(instanceId);
        assertEquals("running", result);
    }

    @Test
    public void testKillInstance() {
        String instanceId = "testInstanceId";
        CloudVmStatus mockStatus = mock(CloudVmStatus.class);
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        VMStatus mockVmStatus = VMStatus.terminated;
        when(mockStatus.getVmStatus()).thenReturn(mockVmStatus);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }
            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
            @Override
            public CloudVmStatus getInstanceStatus(String instanceId) {
                return mockStatus;
            }
        };
        String result = agentService.killInstance(instanceId);
        assertEquals("terminated", result);
    }

    @Test
    public void testEachInstanceActionException() {
        String instanceId = "testInstanceId";
        JobEventSender mockJobEventSender = mock(JobEventSender.class);

        AgentServiceV2Impl agentService = new AgentServiceV2Impl() {
            @Override
            protected void annotateXRaySegment(String key, String value) {
                // do nothing
            }
            @Override
            protected JobEventSender createJobEventSender() {
                return mockJobEventSender;
            }
        };

        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).stopAgent(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> {
            agentService.stopInstance(instanceId);
        });

        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).stopAgent(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> {
            agentService.pauseInstance(instanceId);
        });

        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).stopAgent(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> {
            agentService.resumeInstance(instanceId);
        });

        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).stopAgent(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> {
            agentService.killInstance(instanceId);
        });
    }
}

