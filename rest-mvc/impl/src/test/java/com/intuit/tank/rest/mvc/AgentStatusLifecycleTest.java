package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AgentStatusLifecycleTest {

    @Mock
    private VMTracker vmTracker;

    @Mock
    private VMTerminator terminator;

    @InjectMocks
    private AgentStatusLifecycle lifecycle;

    @Test
    void testRunningStatusUpdatesTrackerWithoutTermination() {
        CloudVmStatus status = createStatus("i-running", JobStatus.Running, VMStatus.running);

        lifecycle.setVmStatus("i-running", status);

        verify(vmTracker).setStatus(status);
        verify(terminator, never()).terminate("i-running");
    }

    @Test
    void testCompletedStatusTriggersTerminationAndEnforcesInstanceId() {
        CloudVmStatus status = createStatus("i-spoofed", JobStatus.Completed, VMStatus.running);

        lifecycle.setVmStatus("i-done", status);

        verify(vmTracker).setStatus(argThat(updated -> "i-done".equals(updated.getInstanceId())
                && updated.getJobStatus() == JobStatus.Completed));
        verify(terminator).terminate("i-done");
        verify(vmTracker).getVmStatusForJob("123");
        assertEquals("i-done", status.getInstanceId());
    }

    @Test
    void testTerminatedVmStatusTriggersTermination() {
        CloudVmStatus status = createStatus("i-term", JobStatus.Running, VMStatus.terminated);

        lifecycle.setVmStatus("i-term", status);

        verify(vmTracker).setStatus(status);
        verify(terminator).terminate("i-term");
    }

    private CloudVmStatus createStatus(String instanceId, JobStatus jobStatus, VMStatus vmStatus) {
        return new CloudVmStatus(
                instanceId,
                "123",
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
