package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.storage.FileStorage;
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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ControllerInitiatedAgentWsClientTest {

    @Test
    void testStartupBootstrapSendsCustomScriptBeforeHarnessJar() {
        byte[] startupScript = "#!/bin/sh\necho custom\n".getBytes();
        byte[] harnessJar = new byte[] { 1, 2, 3 };

        List<ControllerInitiatedAgentWsClient.TransferFile> files =
                ControllerInitiatedAgentWsClient.buildStartupBootstrapFiles(
                        harnessJar, Optional.of(startupScript));

        assertEquals(2, files.size());
        assertEquals("startup_script", files.get(0).fileType());
        assertEquals("startAgent.sh", files.get(0).fileName());
        assertArrayEquals(startupScript, files.get(0).content());
        assertEquals("support_jar", files.get(1).fileType());
        assertEquals("apiharness-1.0-all.jar", files.get(1).fileName());
        assertArrayEquals(harnessJar, files.get(1).content());
    }

    @Test
    void testLegacyAgentRejectionTriggersFallbackOnlyForStartupScript() {
        ControllerInitiatedAgentWsClient.TransferFile startupScript =
                new ControllerInitiatedAgentWsClient.TransferFile(
                        "startup_script", "startAgent.sh", new byte[] { 1 }, false);
        ControllerInitiatedAgentWsClient.TransferFile harnessJar =
                new ControllerInitiatedAgentWsClient.TransferFile(
                        "support_jar", "apiharness-1.0-all.jar", new byte[] { 2 }, false);
        AgentWsEnvelope unsupported = AgentWsEnvelope.fileAck(
                "i-agent", "bootstrap", "file-id", null,
                AgentWsEnvelope.AckStatus.failed, "unsupported_startup_file");
        AgentWsEnvelope otherFailure = AgentWsEnvelope.fileAck(
                "i-agent", "bootstrap", "file-id", null,
                AgentWsEnvelope.AckStatus.failed, "disk_full");

        assertTrue(ControllerInitiatedAgentWsClient.isLegacyStartupScriptRejection(
                startupScript, unsupported));
        assertFalse(ControllerInitiatedAgentWsClient.isLegacyStartupScriptRejection(
                harnessJar, unsupported));
        assertFalse(ControllerInitiatedAgentWsClient.isLegacyStartupScriptRejection(
                startupScript, otherFailure));
    }

    @Test
    void testStartupBootstrapUsesPackagedScriptWhenCustomScriptIsMissing() {
        byte[] harnessJar = new byte[] { 1, 2, 3 };

        List<ControllerInitiatedAgentWsClient.TransferFile> files =
                ControllerInitiatedAgentWsClient.buildStartupBootstrapFiles(
                        harnessJar, Optional.empty());

        assertEquals(1, files.size());
        assertEquals("support_jar", files.get(0).fileType());
        assertEquals("apiharness-1.0-all.jar", files.get(0).fileName());
    }

    @Test
    void testStartupScriptIsReadFromConfiguredFileStorage() throws Exception {
        byte[] startupScript = "#!/bin/sh\necho configured\n".getBytes();
        FileStorage fileStorage = mock(FileStorage.class);
        when(fileStorage.exists(argThat(file -> "startAgent.sh".equals(file.getFileName())))).thenReturn(true);
        when(fileStorage.readFileData(argThat(file -> "startAgent.sh".equals(file.getFileName()))))
                .thenReturn(new ByteArrayInputStream(startupScript));

        Optional<byte[]> result = ControllerInitiatedAgentWsClient.readStartupScript(fileStorage);

        assertTrue(result.isPresent());
        assertArrayEquals(startupScript, result.orElseThrow());
    }

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
    void testStatusUpdateUsesSessionBoundInstanceId() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        VMTracker vmTracker = mock(VMTracker.class);
        VMTerminator vmTerminator = mock(VMTerminator.class);
        client.setVmTracker(vmTracker);
        client.setVmTerminator(vmTerminator);

        CloudVmStatus status = createStatus("i-spoofed", JobStatus.Completed, VMStatus.terminated);
        AgentWsEnvelope statusUpdate = AgentWsEnvelope.statusUpdate("i-spoofed", "job-1", status);

        invokeHandleText(client, "i-session-bound", statusUpdate);

        verify(vmTerminator).terminate("i-session-bound");
        verify(vmTerminator, never()).terminate("i-spoofed");
        verify(vmTracker).setStatus(argThat(updated -> "i-session-bound".equals(updated.getInstanceId())));
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

    private void invokeHandleText(ControllerInitiatedAgentWsClient client, String instanceId,
                                  AgentWsEnvelope envelope) throws Exception {
        Method method = ControllerInitiatedAgentWsClient.class.getDeclaredMethod(
                "handleText", String.class, Session.class, String.class, CompletableFuture.class);
        method.setAccessible(true);
        method.invoke(client, instanceId, null, envelope.toJson(), new CompletableFuture<AgentWsEnvelope>());
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
