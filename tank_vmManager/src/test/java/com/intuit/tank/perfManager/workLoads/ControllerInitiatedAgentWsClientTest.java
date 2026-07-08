package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ControllerInitiatedAgentWsClientTest {

    @Test
    void testTerminalStatusSchedulesTerminationAndUpdatesTracker() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        VMTracker vmTracker = mock(VMTracker.class);
        VMTerminator vmTerminator = mock(VMTerminator.class);
        client.setVmTracker(vmTracker);
        client.setVmTerminator(vmTerminator);

        CloudVmStatus status = createStatus("i-spoofed", JobStatus.Completed, VMStatus.terminated);
        invokeHandleStatusUpdate(client, "i-agent", AgentWsEnvelope.statusUpdate("i-agent", "job-1", status));

        verify(vmTerminator).terminate("i-agent");
        verify(vmTracker).setStatus(argThat(updated -> "i-agent".equals(updated.getInstanceId())
                && updated.getJobStatus() == JobStatus.Completed
                && updated.getVmStatus() == VMStatus.terminated));
        assertEquals("i-agent", status.getInstanceId());
    }

    @Test
    void testTerminalStatusSchedulesTerminationOnlyOnce() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        VMTracker vmTracker = mock(VMTracker.class);
        VMTerminator vmTerminator = mock(VMTerminator.class);
        client.setVmTracker(vmTracker);
        client.setVmTerminator(vmTerminator);

        CloudVmStatus status = createStatus("i-agent", JobStatus.Completed, VMStatus.terminated);
        AgentWsEnvelope statusUpdate = AgentWsEnvelope.statusUpdate("i-agent", "job-1", status);

        invokeHandleStatusUpdate(client, "i-agent", statusUpdate);
        invokeHandleStatusUpdate(client, "i-agent", statusUpdate);

        verify(vmTerminator, times(1)).terminate("i-agent");
        verify(vmTracker, times(2)).setStatus(status);
    }

    @Test
    void testSessionCloseClearsTerminationDedup() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        VMTracker vmTracker = mock(VMTracker.class);
        VMTerminator vmTerminator = mock(VMTerminator.class);
        client.setVmTracker(vmTracker);
        client.setVmTerminator(vmTerminator);

        CloudVmStatus status = createStatus("i-agent", JobStatus.Completed, VMStatus.terminated);
        AgentWsEnvelope statusUpdate = AgentWsEnvelope.statusUpdate("i-agent", "job-1", status);
        invokeHandleStatusUpdate(client, "i-agent", statusUpdate);

        Session session = mock(Session.class);
        installSession(client, "i-agent", session);
        invokeOnClosed(client, "i-agent", session);
        invokeHandleStatusUpdate(client, "i-agent", statusUpdate);

        verify(vmTerminator, times(2)).terminate("i-agent");
    }

    @Test
    void testNonTerminalStatusUpdatesTrackerWithoutTermination() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        VMTracker vmTracker = mock(VMTracker.class);
        VMTerminator vmTerminator = mock(VMTerminator.class);
        client.setVmTracker(vmTracker);
        client.setVmTerminator(vmTerminator);

        CloudVmStatus status = createStatus("i-agent", JobStatus.Running, VMStatus.running);
        invokeHandleStatusUpdate(client, "i-agent", AgentWsEnvelope.statusUpdate("i-agent", "job-1", status));

        verify(vmTerminator, never()).terminate("i-agent");
        verify(vmTracker).setStatus(status);
    }

    private void invokeHandleStatusUpdate(ControllerInitiatedAgentWsClient client, String instanceId,
                                          AgentWsEnvelope envelope) throws Exception {
        Method method = ControllerInitiatedAgentWsClient.class.getDeclaredMethod(
                "handleStatusUpdate", String.class, AgentWsEnvelope.class);
        method.setAccessible(true);
        method.invoke(client, instanceId, envelope);
    }

    private void invokeOnClosed(ControllerInitiatedAgentWsClient client, String instanceId, Session session) throws Exception {
        Method method = ControllerInitiatedAgentWsClient.class.getDeclaredMethod(
                "onClosed", String.class, Session.class);
        method.setAccessible(true);
        method.invoke(client, instanceId, session);
    }

    @SuppressWarnings("unchecked")
    private void installSession(ControllerInitiatedAgentWsClient client, String instanceId, Session session) throws Exception {
        Class<?> sessionContextClass = Class.forName(
                "com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient$SessionContext");
        Class<?> endpointClass = Class.forName(
                "com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient$Endpoint");

        Constructor<?> endpointConstructor = endpointClass.getDeclaredConstructor(
                ControllerInitiatedAgentWsClient.class, String.class, CompletableFuture.class);
        endpointConstructor.setAccessible(true);
        Object endpoint = endpointConstructor.newInstance(client, instanceId, new CompletableFuture<AgentWsEnvelope>());

        Constructor<?> sessionContextConstructor = sessionContextClass.getDeclaredConstructor(
                Session.class, endpointClass, CompletableFuture.class);
        sessionContextConstructor.setAccessible(true);
        Object sessionContext = sessionContextConstructor.newInstance(session, endpoint, new CompletableFuture<AgentWsEnvelope>());

        Field sessionsField = ControllerInitiatedAgentWsClient.class.getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        ConcurrentHashMap<String, Object> sessions =
                (ConcurrentHashMap<String, Object>) sessionsField.get(client);
        sessions.put(instanceId, sessionContext);
    }

    private CloudVmStatus createStatus(String instanceId, JobStatus jobStatus, VMStatus vmStatus) {
        return new CloudVmStatus(
                instanceId,
                "job-1",
                "sg-1",
                jobStatus,
                VMImageType.AGENT,
                VMRegion.US_EAST,
                vmStatus,
                new ValidationStatus(),
                5,
                jobStatus == JobStatus.Completed ? 0 : 1,
                new Date(),
                jobStatus == JobStatus.Completed ? new Date() : null);
    }
}
