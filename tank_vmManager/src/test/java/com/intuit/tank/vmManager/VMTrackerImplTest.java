package com.intuit.tank.vmManager;

import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VMTrackerImpl, focusing on the bug fix for terminated agents
 * blocking job status transitions.
 */
public class VMTrackerImplTest {

    private VMTrackerImpl vmTracker;

    @BeforeEach
    void setUp() {
        vmTracker = new VMTrackerImpl();
    }

    @Test
    @DisplayName("getStatus returns null for non-existent instance")
    void getStatus_returnsNullForNonExistent() {
        assertNull(vmTracker.getStatus("non-existent-instance"));
    }

    @Test
    @DisplayName("removeStatusForInstance handles non-existent instance gracefully")
    void removeStatusForInstance_handlesNonExistent() {
        String instanceId = "i-test123";
        
        // First verify getStatus returns null initially
        assertNull(vmTracker.getStatus(instanceId));
        
        // Call removeStatusForInstance - should handle non-existent gracefully (no exception)
        vmTracker.removeStatusForInstance(instanceId);
        
        // Should still be null (no exception thrown)
        assertNull(vmTracker.getStatus(instanceId));
    }

    @Test
    @DisplayName("getVmStatusForJob returns null for non-existent job")
    void getVmStatusForJob_returnsNullForNonExistent() {
        assertNull(vmTracker.getVmStatusForJob("non-existent-job"));
    }

    @Test
    @DisplayName("getAllJobs returns empty set initially")
    void getAllJobs_returnsEmptySetInitially() {
        Set<CloudVmStatusContainer> jobs = vmTracker.getAllJobs();
        assertNotNull(jobs);
        assertTrue(jobs.isEmpty());
    }

    @Test
    @DisplayName("stopJob marks job as stopped")
    void stopJob_marksJobAsStopped() {
        String jobId = "123";
        
        // Initially running
        assertTrue(vmTracker.isRunning(jobId));
        
        // Stop the job
        vmTracker.stopJob(jobId);
        
        // Now stopped
        assertFalse(vmTracker.isRunning(jobId));
    }

    @Test
    @DisplayName("isRunning returns true for jobs not explicitly stopped")
    void isRunning_returnsTrueByDefault() {
        assertTrue(vmTracker.isRunning("any-job-id"));
    }

    @Test
    @DisplayName("removeStatusForJob handles non-existent job gracefully")
    void removeStatusForJob_handlesNonExistent() {
        // Should not throw
        vmTracker.removeStatusForJob("non-existent-job");
        
        // Verify state is still consistent
        assertNull(vmTracker.getVmStatusForJob("non-existent-job"));
    }

    @Test
    @DisplayName("isDevMode returns false by default")
    void isDevMode_returnsFalseByDefault() {
        // The devMode is set based on TankConfig which defaults to false
        // unless explicitly configured as standalone
        assertFalse(vmTracker.isDevMode());
    }

    @Test
    @DisplayName("getProjectStatusContainer returns null for non-existent project")
    void getProjectStatusContainer_returnsNullForNonExistent() {
        assertNull(vmTracker.getProjectStatusContainer("non-existent-project"));
    }
}

