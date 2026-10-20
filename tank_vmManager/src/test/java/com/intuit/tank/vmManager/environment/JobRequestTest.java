package com.intuit.tank.vmManager.environment;

import com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JobRequestTest {

    @Test
    void limitsConcurrentAgentBootstraps() throws Exception {
        int agents = JobRequest.MAX_CONCURRENT_BOOTSTRAPS * 3;
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger maxInFlight = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(agents);

        ControllerInitiatedAgentWsClient wsClient = mock(ControllerInitiatedAgentWsClient.class);
        when(wsClient.connectAndBootstrap(any(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
                    Thread.sleep(50);
                    inFlight.decrementAndGet();
                    done.countDown();
                    return true;
                });

        VMJobRequest vmJobRequest = mock(VMJobRequest.class);
        when(vmJobRequest.getRegion()).thenReturn(VMRegion.US_WEST_2);
        JobRequest jobRequest = new JobRequest(vmJobRequest, mock(VMTracker.class), mock(JobManager.class));
        Method connect = JobRequest.class.getDeclaredMethod("connectAndBootstrapAgent",
                ControllerInitiatedAgentWsClient.class, VMInstanceRequest.class, VMInformation.class,
                String.class, int.class, long.class, long.class, long.class);
        connect.setAccessible(true);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < agents; i++) {
            VMInformation info = new VMInformation();
            info.setInstanceId("i-" + i);
            info.setPublicIp("10.0.0." + i);
            Thread thread = new Thread(() -> {
                try {
                    connect.invoke(jobRequest, wsClient, new VMInstanceRequest(), info, "token", 8090,
                            1_000L, 1_000L, 60_000L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            threads.add(thread);
            thread.start();
        }

        assertTrue(done.await(30, TimeUnit.SECONDS));
        for (Thread thread : threads) {
            thread.join();
        }
        assertEquals(JobRequest.MAX_CONCURRENT_BOOTSTRAPS, maxInFlight.get());
    }
}
