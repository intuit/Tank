package com.intuit.tank.scheduleManager.jms;

import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.agent.messages.WatsAgentCompletedResponse;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.RequestAgents;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import com.intuit.tank.vm.vmManager.VmMessageProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgentDispatcherImplTest {

    @Mock
    private VmMessageProcessor vmProcessor;

    @InjectMocks
    private AgentDispatcherImpl dispatcher;

    @Test
    public void vmManagerRequest_forwardsToProcessor() {
        VMRequest request = new VMKillRequest(VMProvider.Amazon, "i-1");

        dispatcher.vmManagerRequest(request);

        verify(vmProcessor).handleVMRequest(request);
    }

    @Test
    public void processAgentsMessage_buildsJobRequest() {
        RequestAgents agents = new RequestAgents("7", "reporting", "STANDARD", VMRegion.US_WEST_2,
                IncrementStrategy.increasing, 100, "END_OF_TEST");
        agents.setVmInstanceType("c5.large");
        agents.setNumUsersPerAgent(25);
        agents.setUserEips(true);

        dispatcher.processAgentsMessage(agents);

        ArgumentCaptor<VMRequest> captor = ArgumentCaptor.forClass(VMRequest.class);
        verify(vmProcessor).handleVMRequest(captor.capture());
        VMJobRequest jobRequest = (VMJobRequest) captor.getValue();
        assertEquals("7", jobRequest.getJobId());
        assertEquals("reporting", jobRequest.getReportingMode());
        assertEquals("STANDARD", jobRequest.getLoggingProfile());
        assertEquals(100, jobRequest.getNumberOfUsers());
        assertEquals(VMRegion.US_WEST_2, jobRequest.getRegion());
        assertEquals(IncrementStrategy.increasing, jobRequest.getIncrementStrategy());
        assertEquals("END_OF_TEST", jobRequest.getStopBehavior());
        assertEquals("c5.large", jobRequest.getVmInstanceType());
        assertEquals(25, jobRequest.getNumUsersPerAgent());
        assertTrue(jobRequest.isUseEips());
    }

    @Test
    public void processAgentsMessage_swallowsErrors() {
        RequestAgents agents = mock(RequestAgents.class);
        when(agents.getJobId()).thenThrow(new IllegalStateException("bad request"));

        assertDoesNotThrow(() -> dispatcher.processAgentsMessage(agents));

        verifyNoInteractions(vmProcessor);
    }

    @Test
    public void processAgentCompletedResponse_marksImageCompletedAndKillsInstance() {
        VMInstance image = mock(VMInstance.class);
        try (MockedConstruction<VMImageDao> daoMock = mockConstruction(VMImageDao.class,
                (dao, ctx) -> when(dao.getImageByInstanceId("i-1")).thenReturn(image))) {
            dispatcher.processAgentCompletedResponse(new WatsAgentCompletedResponse(VMProvider.Amazon, "i-1"));

            verify(daoMock.constructed().get(0)).saveOrUpdate(image);
        }
        verify(image).setStatus("Completed");

        ArgumentCaptor<VMRequest> captor = ArgumentCaptor.forClass(VMRequest.class);
        verify(vmProcessor).handleVMRequest(captor.capture());
        VMKillRequest killRequest = (VMKillRequest) captor.getValue();
        assertEquals(VMProvider.Amazon, killRequest.getProvider());
        assertEquals(List.of("i-1"), killRequest.getInstances());
    }

    @Test
    public void processAgentCompletedResponse_missingInstanceIdOrProvider_skipsDatabase() {
        try (MockedConstruction<VMImageDao> daoMock = mockConstruction(VMImageDao.class)) {
            dispatcher.processAgentCompletedResponse(new WatsAgentCompletedResponse(null, null));
            dispatcher.processAgentCompletedResponse(new WatsAgentCompletedResponse(VMProvider.Amazon, null));
            dispatcher.processAgentCompletedResponse(new WatsAgentCompletedResponse(null, "i-1"));

            assertTrue(daoMock.constructed().isEmpty());
        }
        // the kill request is still sent each time
        verify(vmProcessor, times(3)).handleVMRequest(any(VMKillRequest.class));
    }
}
