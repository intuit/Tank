package com.intuit.tank.vmManager;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.VMUpdateStateRequest;
import com.intuit.tank.vmManager.environment.CreateInstance;
import com.intuit.tank.vmManager.environment.JobRequest;
import com.intuit.tank.vmManager.environment.KillInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for VmMessageProcessorImpl. Each request type is dispatched to its runnable inside the
 * current X-Ray trace entity, so a no-op segment is opened for every test.
 */
@ExtendWith(MockitoExtension.class)
public class VmMessageProcessorImplTest {

    @Mock
    private VMTracker vmTracker;
    @Mock
    private JobManager jobManager;
    @InjectMocks
    private VmMessageProcessorImpl processor;

    @BeforeEach
    void beginSegment() {
        AWSXRay.getGlobalRecorder().beginNoOpSegment();
    }

    @AfterEach
    void endSegment() {
        AWSXRay.getGlobalRecorder().clearTraceEntity();
    }

    @Test
    public void instanceRequest_runsCreateInstance() {
        VMInstanceRequest request = new VMInstanceRequest();
        try (MockedConstruction<CreateInstance> create = mockConstruction(CreateInstance.class, (mock, ctx) -> {
            assertSame(request, ctx.arguments().get(0));
            assertSame(vmTracker, ctx.arguments().get(1));
        })) {
            processor.handleVMRequest(request);

            verify(create.constructed().get(0)).run();
        }
    }

    @Test
    public void jobRequest_runsJobRequestWithJobManager() {
        VMJobRequest request = new VMJobRequest("1", "reporting", "STANDARD", 10, VMRegion.US_WEST_2,
                IncrementStrategy.increasing, "END_OF_TEST", "c5.large", 10);
        try (MockedConstruction<JobRequest> job = mockConstruction(JobRequest.class, (mock, ctx) -> {
            assertSame(request, ctx.arguments().get(0));
            assertSame(jobManager, ctx.arguments().get(2));
        })) {
            processor.handleVMRequest(request);

            verify(job.constructed().get(0)).run();
        }
    }

    @Test
    public void killRequest_runsKillInstance() {
        VMKillRequest request = new VMKillRequest(VMProvider.Amazon, "i-1");
        try (MockedConstruction<KillInstance> kill = mockConstruction(KillInstance.class)) {
            processor.handleVMRequest(request);

            verify(kill.constructed().get(0)).run();
        }
    }

    @Test
    public void updateStateAndUnknownRequests_doNothing() {
        try (MockedConstruction<CreateInstance> create = mockConstruction(CreateInstance.class);
             MockedConstruction<JobRequest> job = mockConstruction(JobRequest.class);
             MockedConstruction<KillInstance> kill = mockConstruction(KillInstance.class)) {
            processor.handleVMRequest(new VMUpdateStateRequest(VMProvider.Amazon, "i-1"));
            processor.handleVMRequest(new VMRequest() { });

            assertTrue(create.constructed().isEmpty());
            assertTrue(job.constructed().isEmpty());
            assertTrue(kill.constructed().isEmpty());
        }
        verifyNoInteractions(vmTracker, jobManager);
    }
}
