package com.intuit.tank.harness;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class APIMonitorStaggerTest {

    @Test
    void staggerDelay_isWithinIntervalAndVaries() {
        long interval = 30_000L;
        Set<Long> delays = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            long delay = APIMonitor.staggerDelay(interval);
            assertTrue(delay >= 0 && delay < interval, "delay " + delay + " outside [0, " + interval + ")");
            delays.add(delay);
        }
        assertTrue(delays.size() > 1, "agents should not all pick the same delay");
    }
}
