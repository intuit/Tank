package com.intuit.tank.vmManager.environment;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KillInstanceTest {

    @Test
    public void run_amazon_killsThroughDefaultRegionInstance() {
        VMKillRequest request = new VMKillRequest(VMProvider.Amazon, "i-1");

        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class)) {
            new KillInstance(request).run();

            assertEquals(1, amazon.constructed().size());
            verify(amazon.constructed().get(0)).kill(request);
        }
    }

    @Test
    public void run_pharos_doesNothing() {
        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class)) {
            new KillInstance(new VMKillRequest(VMProvider.Pharos, "i-1")).run();

            assertTrue(amazon.constructed().isEmpty());
        }
    }

    @Test
    public void run_missingProvider_logsAndDoesNothing() {
        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class)) {
            assertDoesNotThrow(() -> new KillInstance(new VMKillRequest(null, "i-1")).run());

            assertTrue(amazon.constructed().isEmpty());
        }
    }
}
