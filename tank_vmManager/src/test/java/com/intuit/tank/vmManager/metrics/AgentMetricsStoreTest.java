package com.intuit.tank.vmManager.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentMetricsStoreTest {

    private static final Instant T0 = Instant.parse("2026-11-06T19:00:00Z");

    private AgentMetricsStore store;

    @BeforeEach
    public void setUp() {
        store = new AgentMetricsStore();
    }

    @Test
    public void unknownJobHasNoSnapshot() {
        assertNull(store.snapshot("1"));
    }

    @Test
    public void agentsShareOneTimeAxisWithNullsWhereTheyHaveNoSample() {
        store.record("1", "i-a", AgentMetric.CPU, T0, 10);
        store.record("1", "i-a", AgentMetric.CPU, T0.plusSeconds(60), 20);
        store.record("1", "i-b", AgentMetric.CPU, T0.plusSeconds(60), 30);
        store.markFresh("1", T0.plusSeconds(90));

        AgentMetricsSnapshot snapshot = store.snapshot("1");

        assertEquals(List.of(T0, T0.plusSeconds(60)), snapshot.times());
        assertEquals(T0.plusSeconds(90), snapshot.generatedAt());
        assertFalse(snapshot.stale());
        assertEquals(List.of("i-a", "i-b"), snapshot.agents().stream().map(AgentMetricsSnapshot.Agent::instanceId).toList());
        assertEquals(java.util.Arrays.asList(10d, 20d), snapshot.agents().get(0).metrics().get(AgentMetric.CPU));
        assertEquals(java.util.Arrays.asList(null, 30d), snapshot.agents().get(1).metrics().get(AgentMetric.CPU));
    }

    @Test
    public void metricsAnAgentNeverReportedAreAllNull() {
        store.record("1", "i-a", AgentMetric.CPU, T0, 10);

        AgentMetricsSnapshot.Agent agent = store.snapshot("1").agents().get(0);

        assertEquals(java.util.Arrays.asList((Double) null), agent.metrics().get(AgentMetric.HEAP));
        assertEquals(java.util.Arrays.asList((Double) null), agent.metrics().get(AgentMetric.THREADS));
        assertNull(agent.heapMaxMb());
    }

    @Test
    public void keepsOnlyTheNewestSamplesPerMetric() {
        store.setRetention(3);
        for (int i = 0; i < 5; i++) {
            store.record("1", "i-a", AgentMetric.MEM, T0.plusSeconds(60L * i), i);
        }

        AgentMetricsSnapshot snapshot = store.snapshot("1");

        assertEquals(List.of(T0.plusSeconds(120), T0.plusSeconds(180), T0.plusSeconds(240)), snapshot.times());
        assertEquals(List.of(2d, 3d, 4d), snapshot.agents().get(0).metrics().get(AgentMetric.MEM));
    }

    @Test
    public void aLaterSampleForTheSameTimeReplacesTheEarlier() {
        store.record("1", "i-a", AgentMetric.TCP, T0, 1);
        store.record("1", "i-a", AgentMetric.TCP, T0, 2);

        assertEquals(List.of(2d), store.snapshot("1").agents().get(0).metrics().get(AgentMetric.TCP));
    }

    @Test
    public void staleIsFlaggedUntilTheNextFreshRead() {
        store.record("1", "i-a", AgentMetric.CPU, T0, 1);
        store.markFresh("1", T0);
        store.markStale("1");
        assertTrue(store.snapshot("1").stale());

        store.markFresh("1", T0.plusSeconds(60));

        assertFalse(store.snapshot("1").stale());
    }

    @Test
    public void heapMaxIsKept() {
        store.setHeapMaxMb("1", "i-a", 2048);

        assertEquals(2048d, store.snapshot("1").agents().get(0).heapMaxMb());
    }

    @Test
    public void finishedJobsAreEvictedOnlyAfterTheGracePeriod() {
        store.record("1", "i-a", AgentMetric.CPU, T0, 1);
        store.record("2", "i-b", AgentMetric.CPU, T0, 1);
        store.markFinished("1", T0);

        store.evict(T0.plusSeconds(60), 120_000);
        assertEquals(2, store.jobIds().size(), "within the grace period");

        store.evict(T0.plusSeconds(121), 120_000);
        assertEquals(List.of("2"), store.jobIds(), "only the finished job goes");
    }

    @Test
    public void aJobThatStartsAgainIsNotEvicted() {
        store.record("1", "i-a", AgentMetric.CPU, T0, 1);
        store.markFinished("1", T0);
        store.markFresh("1", T0.plusSeconds(30));

        store.evict(T0.plusSeconds(600), 1000);

        assertEquals(List.of("1"), store.jobIds());
    }
}
