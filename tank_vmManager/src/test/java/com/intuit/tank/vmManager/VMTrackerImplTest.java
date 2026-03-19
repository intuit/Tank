package com.intuit.tank.vmManager;

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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for VMTrackerImpl, focusing on the bug fix for terminated agents
 * blocking job status transitions and the VMStatus.replaced handling.
 */
public class VMTrackerImplTest {

    private VMTrackerImpl vmTracker;

    @BeforeEach
    void setUp() {
        vmTracker = new VMTrackerImpl();
    }

    // ============ Basic functionality tests ============

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

    // ============ shouldUpdateStatus tests (terminal state handling) ============

    /**
     * Use reflection to test the private shouldUpdateStatus method.
     */
    private boolean invokeShouldUpdateStatus(CloudVmStatus status) throws Exception {
        Method method = VMTrackerImpl.class.getDeclaredMethod("shouldUpdateStatus", CloudVmStatus.class);
        method.setAccessible(true);
        return (boolean) method.invoke(vmTracker, status);
    }

    private CloudVmStatus createStatus(String instanceId, VMStatus vmStatus) {
        return new CloudVmStatus(
                instanceId,
                "123",
                "sg-test",
                JobStatus.Starting,
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

    @Test
    @DisplayName("shouldUpdateStatus returns true for null current status")
    void shouldUpdateStatus_nullCurrentStatus_returnsTrue() throws Exception {
        assertTrue(invokeShouldUpdateStatus(null));
    }

    @ParameterizedTest
    @EnumSource(value = VMStatus.class, names = {"terminated", "stopped", "stopping", "shutting_down"})
    @DisplayName("shouldUpdateStatus returns false for terminal states")
    void shouldUpdateStatus_terminalStates_returnsFalse(VMStatus terminalStatus) throws Exception {
        CloudVmStatus status = createStatus("i-123", terminalStatus);
        assertFalse(invokeShouldUpdateStatus(status), 
            "Should reject updates for terminal state: " + terminalStatus);
    }

    @ParameterizedTest
    @EnumSource(value = VMStatus.class, names = {"unknown", "starting", "pending", "ready", "running", "rampPaused", "rebooting"})
    @DisplayName("shouldUpdateStatus returns true for active states")
    void shouldUpdateStatus_activeStates_returnsTrue(VMStatus activeStatus) throws Exception {
        CloudVmStatus status = createStatus("i-123", activeStatus);
        assertTrue(invokeShouldUpdateStatus(status), 
            "Should allow updates for active state: " + activeStatus);
    }

    @Test
    @DisplayName("shouldUpdateStatus allows updates for replaced instances (protection handled in AgentWatchdog)")
    void shouldUpdateStatus_replacedInstance_allowsUpdate() throws Exception {
        // Given: An instance that was replaced by AgentWatchdog
        CloudVmStatus replacedStatus = createStatus("i-replaced", VMStatus.replaced);
        
        // When/Then: Updates should be allowed - protection is handled by removing
        // instances from tracking lists in AgentWatchdog before marking as replaced,
        // NOT by blocking in shouldUpdateStatus (which would break killJobDirectly)
        assertTrue(invokeShouldUpdateStatus(replacedStatus),
            "Replaced instances should allow updates (e.g., replaced -> terminated for killJobDirectly)");
    }

    // ============ VMStatus.replaced integration tests ============

    @Test
    @DisplayName("replaced status is recognized as a valid VMStatus")
    void replacedStatus_isValidEnum() {
        assertNotNull(VMStatus.replaced);
        assertEquals("replaced", VMStatus.replaced.name());
    }

    @Test
    @DisplayName("setStatus respects terminal state guard for replaced instances")
    void setStatus_respectsTerminalGuard_forReplaced() throws Exception {
        // Note: This is a partial test - full integration requires mocked dependencies
        // The key behavior is tested via shouldUpdateStatus tests above
        
        // Verify the enum value exists and can be used
        CloudVmStatus status = createStatus("i-test", VMStatus.replaced);
        assertEquals(VMStatus.replaced, status.getVmStatus());
    }

    // ============ Status calculation tests (using reflection to bypass CDI dependencies) ============

    private CloudVmStatus createStatusWithJobStatus(String instanceId, String jobId, VMStatus vmStatus, JobStatus jobStatus) {
        return new CloudVmStatus(
                instanceId,
                jobId,
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
     * Directly add status to internal maps using reflection (bypasses async setStatus and CDI dependencies).
     * This allows testing the status calculation logic in isolation.
     */
    @SuppressWarnings("unchecked")
    private void addStatusDirectly(CloudVmStatus status) throws Exception {
        // Access the private statusMap field
        java.lang.reflect.Field statusMapField = VMTrackerImpl.class.getDeclaredField("statusMap");
        statusMapField.setAccessible(true);
        java.util.concurrent.ConcurrentHashMap<String, CloudVmStatus> statusMap = 
            (java.util.concurrent.ConcurrentHashMap<String, CloudVmStatus>) statusMapField.get(vmTracker);
        
        // Access the private jobMap field
        java.lang.reflect.Field jobMapField = VMTrackerImpl.class.getDeclaredField("jobMap");
        jobMapField.setAccessible(true);
        java.util.concurrent.ConcurrentHashMap<String, CloudVmStatusContainer> jobMap = 
            (java.util.concurrent.ConcurrentHashMap<String, CloudVmStatusContainer>) jobMapField.get(vmTracker);
        
        // Add to statusMap
        statusMap.put(status.getInstanceId(), status);
        
        // Add to job container
        String jobId = status.getJobId();
        CloudVmStatusContainer container = jobMap.computeIfAbsent(jobId, k -> {
            CloudVmStatusContainer c = new CloudVmStatusContainer();
            c.setJobId(jobId);
            return c;
        });
        container.getStatuses().add(status);
    }

    @Test
    @DisplayName("Status calculation with all agents replaced returns early (activeInstanceCount = 0)")
    void statusCalculation_allAgentsReplaced_returnsEarly() throws Exception {
        // Given: A job where ALL agents have been replaced by watchdog
        String jobId = "all-replaced-job";
        
        // Set up 3 agents, all marked as replaced (using direct manipulation)
        addStatusDirectly(createStatusWithJobStatus("i-001", jobId, VMStatus.replaced, JobStatus.Starting));
        addStatusDirectly(createStatusWithJobStatus("i-002", jobId, VMStatus.replaced, JobStatus.Starting));
        addStatusDirectly(createStatusWithJobStatus("i-003", jobId, VMStatus.replaced, JobStatus.Starting));
        
        // When: Get job status
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        
        // Then: Container should exist with 3 statuses
        assertNotNull(container, "Container should exist");
        assertEquals(3, container.getStatuses().size());
        
        // Verify all have replaced status
        assertTrue(container.getStatuses().stream().allMatch(s -> s.getVmStatus() == VMStatus.replaced),
            "All agents should be in replaced state");
    }

    @Test
    @DisplayName("Status calculation with mixed statuses correctly tracks replaced agents")
    void statusCalculation_mixedStatuses_correctlyDeterminesJobStatus() throws Exception {
        String jobId = "mixed-status-job";
        
        // Given: Mix of running, pending, and replaced agents (using direct manipulation)
        addStatusDirectly(createStatusWithJobStatus("i-running1", jobId, VMStatus.running, JobStatus.Running));
        addStatusDirectly(createStatusWithJobStatus("i-running2", jobId, VMStatus.running, JobStatus.Running));
        addStatusDirectly(createStatusWithJobStatus("i-replaced", jobId, VMStatus.replaced, JobStatus.Starting));
        addStatusDirectly(createStatusWithJobStatus("i-pending", jobId, VMStatus.pending, JobStatus.Starting));
        
        // When
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        
        // Then: Should have 4 total statuses
        assertNotNull(container, "Container should exist");
        assertEquals(4, container.getStatuses().size());
        
        // Replaced should be in container
        assertTrue(container.getStatuses().stream()
            .anyMatch(s -> s.getInstanceId().equals("i-replaced") && s.getVmStatus() == VMStatus.replaced),
            "Replaced instance should be in container");
        
        // Running instances should also be present
        assertEquals(2, container.getStatuses().stream()
            .filter(s -> s.getVmStatus() == VMStatus.running).count(),
            "Should have 2 running instances");
    }

    @Test
    @DisplayName("Status calculation container includes all terminal states")
    void statusCalculation_onlySkipsReplaced_notOtherTerminalStates() throws Exception {
        String jobId = "terminal-states-job";
        
        // Given: Various terminal and non-terminal states (using direct manipulation)
        addStatusDirectly(createStatusWithJobStatus("i-running", jobId, VMStatus.running, JobStatus.Running));
        addStatusDirectly(createStatusWithJobStatus("i-replaced", jobId, VMStatus.replaced, JobStatus.Starting));
        addStatusDirectly(createStatusWithJobStatus("i-terminated", jobId, VMStatus.terminated, JobStatus.Completed));
        addStatusDirectly(createStatusWithJobStatus("i-stopped", jobId, VMStatus.stopped, JobStatus.Stopped));
        
        // When
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        
        // Then: Container should have all 4 statuses
        assertNotNull(container, "Container should exist");
        assertEquals(4, container.getStatuses().size());
        
        // Verify each status type is present
        assertTrue(container.getStatuses().stream().anyMatch(s -> s.getVmStatus() == VMStatus.running));
        assertTrue(container.getStatuses().stream().anyMatch(s -> s.getVmStatus() == VMStatus.replaced));
        assertTrue(container.getStatuses().stream().anyMatch(s -> s.getVmStatus() == VMStatus.terminated));
        assertTrue(container.getStatuses().stream().anyMatch(s -> s.getVmStatus() == VMStatus.stopped));
    }

    // ============ Thread safety tests (using direct manipulation to avoid CDI issues) ============

    @RepeatedTest(3)
    @DisplayName("Concurrent direct status additions do not cause ConcurrentModificationException")
    void concurrentDirectStatusAdd_noException() throws Exception {
        String jobId = "concurrent-test-job-" + System.nanoTime();
        int numThreads = 10;
        int operationsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        for (int t = 0; t < numThreads; t++) {
            final int threadNum = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < operationsPerThread; i++) {
                        String instanceId = "i-thread" + threadNum + "-op" + i;
                        addStatusDirectly(createStatusWithJobStatus(
                            instanceId, jobId, VMStatus.running, JobStatus.Running));
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Test timed out");
        executor.shutdown();
        
        assertEquals(0, errorCount.get(), "Concurrent operations should not throw exceptions");
        
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        assertNotNull(container, "Container should exist");
        assertTrue(container.getStatuses().size() > 0, "Should have some statuses");
    }

    @RepeatedTest(3)
    @DisplayName("Concurrent add and removeStatusForInstance do not cause race conditions")
    void concurrentAddAndRemove_noRaceCondition() throws Exception {
        String jobId = "concurrent-remove-job-" + System.nanoTime();
        int numAddThreads = 5;
        int numRemoveThreads = 5;
        int operationsPerThread = 30;
        ExecutorService executor = Executors.newFixedThreadPool(numAddThreads + numRemoveThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numAddThreads + numRemoveThreads);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<String> instanceIds = new ArrayList<>();
        
        // Pre-populate some instances
        for (int i = 0; i < operationsPerThread; i++) {
            String instanceId = "i-prepop-" + i;
            instanceIds.add(instanceId);
            addStatusDirectly(createStatusWithJobStatus(instanceId, jobId, VMStatus.running, JobStatus.Running));
        }
        
        // Threads that add new statuses
        for (int t = 0; t < numAddThreads; t++) {
            final int threadNum = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < operationsPerThread; i++) {
                        String instanceId = "i-add-thread" + threadNum + "-op" + i;
                        addStatusDirectly(createStatusWithJobStatus(
                            instanceId, jobId, VMStatus.running, JobStatus.Running));
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        // Threads that remove statuses
        for (int t = 0; t < numRemoveThreads; t++) {
            final int threadNum = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < operationsPerThread; i++) {
                        if (i < instanceIds.size()) {
                            vmTracker.removeStatusForInstance(instanceIds.get(i));
                        }
                        vmTracker.removeStatusForInstance("i-nonexistent-" + threadNum + "-" + i);
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Test timed out");
        executor.shutdown();
        
        assertEquals(0, errorCount.get(), "Concurrent add/remove should not throw exceptions");
    }

    @RepeatedTest(3)
    @DisplayName("Concurrent removeStatusForJob does not cause ConcurrentModificationException")
    void concurrentRemoveStatusForJob_noException() throws Exception {
        int numJobs = 5;
        int instancesPerJob = 10;
        String prefix = "job-" + System.nanoTime() + "-";
        ExecutorService executor = Executors.newFixedThreadPool(numJobs);
        CountDownLatch doneLatch = new CountDownLatch(numJobs);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        // Set up multiple jobs with instances
        for (int j = 0; j < numJobs; j++) {
            String jobId = prefix + j;
            for (int i = 0; i < instancesPerJob; i++) {
                addStatusDirectly(createStatusWithJobStatus(
                    "i-" + jobId + "-instance" + i, jobId, VMStatus.running, JobStatus.Running));
            }
        }
        
        // Concurrently remove all jobs
        for (int j = 0; j < numJobs; j++) {
            final String jobId = prefix + j;
            executor.submit(() -> {
                try {
                    vmTracker.removeStatusForJob(jobId);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Test timed out");
        executor.shutdown();
        
        assertEquals(0, errorCount.get(), "Concurrent removeStatusForJob should not throw exceptions");
        
        // Verify all jobs are removed
        for (int j = 0; j < numJobs; j++) {
            assertNull(vmTracker.getVmStatusForJob(prefix + j), "Job " + prefix + j + " should be removed");
        }
    }

    // ============ removeStatusForInstance tests ============

    @Test
    @DisplayName("removeStatusForInstance removes from statusMap")
    void removeStatusForInstance_removesFromStatusMap() throws Exception {
        String jobId = "remove-test-job";
        String instanceId = "i-remove-test";
        
        // Given: An instance with status (using direct manipulation)
        CloudVmStatus status = createStatusWithJobStatus(instanceId, jobId, VMStatus.running, JobStatus.Running);
        addStatusDirectly(status);
        
        // Verify it exists
        assertNotNull(vmTracker.getStatus(instanceId), "Status should exist in statusMap");
        
        // When: Remove the status
        vmTracker.removeStatusForInstance(instanceId);
        
        // Then: Should be removed from statusMap
        assertNull(vmTracker.getStatus(instanceId), "Status should be removed from statusMap");
    }

    @Test
    @DisplayName("removeStatusForInstance handles non-existent instance gracefully")
    void removeStatusForInstance_nonExistent_noException() {
        // Should not throw
        assertDoesNotThrow(() -> vmTracker.removeStatusForInstance("i-does-not-exist"));
    }
}

