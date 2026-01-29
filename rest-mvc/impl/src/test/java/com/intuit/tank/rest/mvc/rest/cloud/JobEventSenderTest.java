package com.intuit.tank.rest.mvc.rest.cloud;

import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JobEventSender
 */
@ExtendWith(MockitoExtension.class)
public class JobEventSenderTest {

    @Mock
    private VMTracker vmTracker;

    @InjectMocks
    private JobEventSender jobEventSender;

    private CloudVmStatusContainer container;

    @BeforeEach
    void setUp() {
        container = new CloudVmStatusContainer();
        container.setJobId("123");
    }

    // Helper method to create CloudVmStatus
    private CloudVmStatus createStatus(String instanceId, JobStatus jobStatus, VMStatus vmStatus) {
        return new CloudVmStatus(
                instanceId,
                "123",
                "sg-test",
                jobStatus,
                VMImageType.AGENT,
                VMRegion.US_WEST_2,
                vmStatus,
                new ValidationStatus(),
                100,
                50,
                new Date(),
                null
        );
    }

    /**
     * Use reflection to test the private getInstancesForJob method.
     */
    private List<String> invokeGetInstancesForJob(String jobId) throws Exception {
        Method method = JobEventSender.class.getDeclaredMethod("getInstancesForJob", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) method.invoke(jobEventSender, jobId);
        return result;
    }

    @Test
    @DisplayName("getInstancesForJob includes terminated instances (AWS handles idempotently)")
    void getInstancesForJob_includesTerminatedInstances() throws Exception {
        // Given: Mix of running and terminated instances
        container.getStatuses().add(createStatus("i-running1", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-terminated", JobStatus.Stopped, VMStatus.terminated));
        container.getStatuses().add(createStatus("i-running2", JobStatus.Running, VMStatus.running));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: All instances returned (AWS terminateInstances is idempotent)
        assertEquals(3, instances.size());
        assertTrue(instances.contains("i-running1"));
        assertTrue(instances.contains("i-running2"));
        assertTrue(instances.contains("i-terminated"));
    }

    @Test
    @DisplayName("getInstancesForJob includes stopped instances (EC2 still exists)")
    void getInstancesForJob_includesStoppedInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-stopped", JobStatus.Stopped, VMStatus.stopped));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: stopped instances are included (EC2 still exists, needs termination)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-stopped"));
    }

    @Test
    @DisplayName("getInstancesForJob includes stopping instances (EC2 still exists)")
    void getInstancesForJob_includesStoppingInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-stopping", JobStatus.Stopped, VMStatus.stopping));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: stopping instances are included (EC2 still exists)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-stopping"));
    }

    @Test
    @DisplayName("getInstancesForJob includes shutting_down instances (EC2 still exists)")
    void getInstancesForJob_includesShuttingDownInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-shuttingdown", JobStatus.Stopped, VMStatus.shutting_down));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: shutting_down instances are included (EC2 still exists)
        assertEquals(2, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-shuttingdown"));
    }

    @Test
    @DisplayName("getInstancesForJob includes pending instances (agents starting)")
    void getInstancesForJob_includesPendingInstances() throws Exception {
        // Given: Pending instances are still reachable
        container.getStatuses().add(createStatus("i-pending", JobStatus.Starting, VMStatus.pending));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: Pending instances should be included
        assertEquals(1, instances.size());
        assertTrue(instances.contains("i-pending"));
    }

    @Test
    @DisplayName("getInstancesForJob includes starting instances")
    void getInstancesForJob_includesStartingInstances() throws Exception {
        // Given
        container.getStatuses().add(createStatus("i-starting", JobStatus.Starting, VMStatus.starting));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then
        assertEquals(1, instances.size());
        assertTrue(instances.contains("i-starting"));
    }

    @Test
    @DisplayName("getInstancesForJob returns empty list for non-existent job")
    void getInstancesForJob_returnsEmptyForNonExistent() throws Exception {
        // Given
        when(vmTracker.getVmStatusForJob("999")).thenReturn(null);
        
        // When
        List<String> instances = invokeGetInstancesForJob("999");
        
        // Then
        assertNotNull(instances);
        assertTrue(instances.isEmpty());
    }

    @Test
    @DisplayName("getInstancesForJob excludes replaced instances (AgentWatchdog replacements)")
    void getInstancesForJob_excludesReplacedInstances() throws Exception {
        // Given: VMStatus.replaced is set by AgentWatchdog when an agent fails and is replaced
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-replaced", JobStatus.Starting, VMStatus.replaced));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: Replaced instances should be filtered out (can't receive commands)
        assertEquals(1, instances.size());
        assertTrue(instances.contains("i-running"));
        assertFalse(instances.contains("i-replaced"));
    }

    @Test
    @DisplayName("getInstancesForJob filters correctly with mixed statuses from AgentWatchdog scenario")
    void getInstancesForJob_agentWatchdogScenario() throws Exception {
        // Given: Real-world scenario where AgentWatchdog replaced some agents
        // - 3 running agents (healthy)
        // - 2 replaced agents (marked by watchdog with VMStatus.replaced)
        container.getStatuses().add(createStatus("i-healthy1", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-healthy2", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-healthy3", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-replaced1", JobStatus.Starting, VMStatus.replaced));
        container.getStatuses().add(createStatus("i-replaced2", JobStatus.Starting, VMStatus.replaced));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When: User tries to kill the job
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: Only healthy instances should receive the kill command
        assertEquals(3, instances.size());
        assertTrue(instances.contains("i-healthy1"));
        assertTrue(instances.contains("i-healthy2"));
        assertTrue(instances.contains("i-healthy3"));
        assertFalse(instances.contains("i-replaced1"));
        assertFalse(instances.contains("i-replaced2"));
    }

    @Test
    @DisplayName("getInstancesForJob excludes only replaced instances")
    void getInstancesForJob_excludesOnlyReplaced() throws Exception {
        // Given: One instance of each state
        container.getStatuses().add(createStatus("i-running", JobStatus.Running, VMStatus.running));
        container.getStatuses().add(createStatus("i-terminated", JobStatus.Completed, VMStatus.terminated));
        container.getStatuses().add(createStatus("i-replaced", JobStatus.Starting, VMStatus.replaced));
        container.getStatuses().add(createStatus("i-stopped", JobStatus.Stopped, VMStatus.stopped));
        container.getStatuses().add(createStatus("i-stopping", JobStatus.Stopped, VMStatus.stopping));
        container.getStatuses().add(createStatus("i-shutting-down", JobStatus.Stopped, VMStatus.shutting_down));
        
        when(vmTracker.getVmStatusForJob("123")).thenReturn(container);
        
        // When
        List<String> instances = invokeGetInstancesForJob("123");
        
        // Then: Only replaced is excluded (watchdog already killed it on AWS)
        // All others included (EC2 instances exist or AWS handles idempotently)
        assertEquals(5, instances.size());
        assertTrue(instances.contains("i-running"));
        assertTrue(instances.contains("i-terminated"));
        assertTrue(instances.contains("i-stopped"));
        assertTrue(instances.contains("i-stopping"));
        assertTrue(instances.contains("i-shutting-down"));
        assertFalse(instances.contains("i-replaced"));
    }

    @Test
    @DisplayName("getVmStatus delegates to vmTracker")
    void getVmStatus_delegatesToVmTracker() {
        String instanceId = "i-test123";
        CloudVmStatus expectedStatus = createStatus(instanceId, JobStatus.Running, VMStatus.running);
        when(vmTracker.getStatus(instanceId)).thenReturn(expectedStatus);
        
        CloudVmStatus result = jobEventSender.getVmStatus(instanceId);
        
        assertEquals(expectedStatus, result);
        verify(vmTracker).getStatus(instanceId);
    }

    @Test
    @DisplayName("getVmStatusForJob delegates to vmTracker")
    void getVmStatusForJob_delegatesToVmTracker() {
        String jobId = "123";
        when(vmTracker.getVmStatusForJob(jobId)).thenReturn(container);
        
        CloudVmStatusContainer result = jobEventSender.getVmStatusForJob(jobId);
        
        assertEquals(container, result);
        verify(vmTracker).getVmStatusForJob(jobId);
    }
}

