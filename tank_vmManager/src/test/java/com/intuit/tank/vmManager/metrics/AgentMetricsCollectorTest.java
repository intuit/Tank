package com.intuit.tank.vmManager.metrics;

import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vmManager.environment.amazon.CloudWatchClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class AgentMetricsCollectorTest {

    private static final Instant NOW = Instant.parse("2026-11-06T19:22:30Z");
    private static final Instant SAMPLE = Instant.parse("2026-11-06T19:22:00Z");
    private static final AgentMetricsCollector.Settings SETTINGS = new AgentMetricsCollector.Settings(true, 60, 60, 15, 0);

    private final VMTracker tracker = mock(VMTracker.class);
    private final CloudWatchClientFactory factory = mock(CloudWatchClientFactory.class);
    private final CloudWatchClient west = mock(CloudWatchClient.class);
    private final CloudWatchClient east = mock(CloudWatchClient.class);
    private AgentMetricsStore store;
    private MutableClock clock;
    private Set<CloudVmStatusContainer> jobs;

    @BeforeEach
    public void setUp() {
        store = new AgentMetricsStore();
        clock = new MutableClock(NOW);
        jobs = new LinkedHashSet<>();
        when(tracker.getAllJobs()).thenAnswer(i -> jobs);
        when(factory.forRegion(VMRegion.US_WEST_2)).thenReturn(west);
        when(factory.forRegion(VMRegion.US_EAST)).thenReturn(east);
        for (CloudWatchClient client : List.of(west, east)) {
            when(client.listMetrics(any(ListMetricsRequest.class))).thenAnswer(i -> allMetrics(i.getArgument(0)));
            when(client.getMetricData(any(GetMetricDataRequest.class))).thenAnswer(i -> data(i.getArgument(0), 55));
        }
    }

    private AgentMetricsCollector collector(java.util.function.Predicate<String> owns) {
        return new AgentMetricsCollector(tracker, store, factory, clock, SETTINGS, owns);
    }

    @Test
    public void readsEachRegionOnceAndStoresTheFigures() {
        job("1", agent("i-w1", VMRegion.US_WEST_2), agent("i-w2", VMRegion.US_WEST_2), agent("i-e1", VMRegion.US_EAST));

        collector(id -> true).tick();

        verify(west, times(1)).getMetricData(any(GetMetricDataRequest.class));
        verify(east, times(1)).getMetricData(any(GetMetricDataRequest.class));
        AgentMetricsSnapshot snapshot = store.snapshot("1");
        assertEquals(List.of(SAMPLE), snapshot.times());
        assertEquals(3, snapshot.agents().size());
        AgentMetricsSnapshot.Agent agent = snapshot.agents().get(0);
        assertEquals(List.of(55d), agent.metrics().get(AgentMetric.CPU));
        assertEquals(List.of(60d), agent.metrics().get(AgentMetric.MEM));
        assertEquals(List.of(25d), agent.metrics().get(AgentMetric.HEAP), "512 of 2048 MB");
        assertEquals(2048d, agent.heapMaxMb());
        assertEquals(List.of(700d), agent.metrics().get(AgentMetric.THREADS));
        assertEquals(List.of(30d), agent.metrics().get(AgentMetric.GC));
        assertEquals(List.of(1500d), agent.metrics().get(AgentMetric.TCP));
        assertFalse(snapshot.stale());
        assertEquals(NOW, snapshot.generatedAt());
    }

    @Test
    public void groupsInstancesOfAllJobsIntoOneCallPerRegion() {
        job("1", agent("i-1", VMRegion.US_WEST_2));
        job("2", agent("i-2", VMRegion.US_WEST_2));

        collector(id -> true).tick();

        ArgumentCaptor<GetMetricDataRequest> request = ArgumentCaptor.forClass(GetMetricDataRequest.class);
        verify(west, times(1)).getMetricData(request.capture());
        assertEquals(14, request.getValue().metricDataQueries().size(), "7 sources for each of 2 agents");
        assertEquals(1, store.snapshot("1").agents().size());
        assertEquals(1, store.snapshot("2").agents().size());
    }

    @Test
    public void splitsQueriesIntoCallsOfAtMost500() {
        List<CloudVmStatus> many = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            many.add(agent("i-" + i, VMRegion.US_WEST_2));
        }
        job("1", many.toArray(new CloudVmStatus[0]));

        collector(id -> true).tick();

        ArgumentCaptor<GetMetricDataRequest> requests = ArgumentCaptor.forClass(GetMetricDataRequest.class);
        verify(west, times(2)).getMetricData(requests.capture());
        assertEquals(500, requests.getAllValues().get(0).metricDataQueries().size());
        assertEquals(60, requests.getAllValues().get(1).metricDataQueries().size(), "80 x 7 = 560 queries");
        assertEquals(80, store.snapshot("1").agents().size());
    }

    @Test
    public void usesTheExactDimensionsListMetricsFound() {
        job("1", agent("i-1", VMRegion.US_WEST_2));

        collector(id -> true).tick();

        ArgumentCaptor<GetMetricDataRequest> request = ArgumentCaptor.forClass(GetMetricDataRequest.class);
        verify(west).getMetricData(request.capture());
        Map<String, List<Dimension>> byName = request.getValue().metricDataQueries().stream().collect(Collectors.toMap(
                q -> q.metricStat().metric().metricName(), q -> q.metricStat().metric().dimensions()));
        assertEquals(List.of(dim("InstanceId", "i-1"), dim("Service", "TankAgent"), dim("exe", "java"),
                dim("pid_finder", "native")), byName.get("procstat_cpu_usage"), "the java process, not a stray one");
        assertEquals("CWAgent", request.getValue().metricDataQueries().get(0).metricStat().metric().namespace());
        assertEquals(60, request.getValue().metricDataQueries().get(0).metricStat().period());
    }

    @Test
    public void resolvesDimensionsOncePerAgentAndReadsIncrementally() {
        job("1", agent("i-1", VMRegion.US_WEST_2));
        AgentMetricsCollector collector = collector(id -> true);

        collector.tick();
        clock.set(NOW.plusSeconds(60));
        collector.tick();

        verify(west, times(1)).listMetrics(any(ListMetricsRequest.class));
        ArgumentCaptor<GetMetricDataRequest> requests = ArgumentCaptor.forClass(GetMetricDataRequest.class);
        verify(west, times(2)).getMetricData(requests.capture());
        assertEquals(NOW.minusSeconds(600), requests.getAllValues().get(0).startTime());
        assertEquals(SAMPLE.minusSeconds(120), requests.getAllValues().get(1).startTime(), "newest sample less the overlap");
    }

    @Test
    public void agentsWithoutJvmMetricsGetNullSeriesAndAreLookedUpAgainLater() {
        when(west.listMetrics(any(ListMetricsRequest.class))).thenAnswer(i -> metrics(i.getArgument(0),
                "procstat_cpu_usage", "mem_used_percent", "tcp_established"));
        job("1", agent("i-old", VMRegion.US_WEST_2));
        AgentMetricsCollector collector = collector(id -> true);

        collector.tick();

        ArgumentCaptor<GetMetricDataRequest> request = ArgumentCaptor.forClass(GetMetricDataRequest.class);
        verify(west).getMetricData(request.capture());
        assertEquals(3, request.getValue().metricDataQueries().size(), "only what the agent publishes is queried");
        AgentMetricsSnapshot.Agent agent = store.snapshot("1").agents().get(0);
        assertEquals(Arrays.asList((Double) null), agent.metrics().get(AgentMetric.HEAP));
        assertEquals(Arrays.asList((Double) null), agent.metrics().get(AgentMetric.THREADS));
        assertNull(agent.heapMaxMb());
        assertEquals(List.of(55d), agent.metrics().get(AgentMetric.CPU));

        clock.set(NOW.plusSeconds(60));
        collector.tick();
        verify(west, times(1)).listMetrics(any(ListMetricsRequest.class));
        clock.set(NOW.plusSeconds(200));
        collector.tick();
        verify(west, times(2)).listMetrics(any(ListMetricsRequest.class));
    }

    @Test
    public void cpuIsDividedByVcpusWhenKnownElseCappedAt100() {
        when(west.getMetricData(any(GetMetricDataRequest.class))).thenAnswer(i -> data(i.getArgument(0), 180));
        job("1", agent("i-1", VMRegion.US_WEST_2));

        collector(id -> true).tick();
        assertEquals(List.of(100d), store.snapshot("1").agents().get(0).metrics().get(AgentMetric.CPU));

        AgentMetricsStore other = new AgentMetricsStore();
        new AgentMetricsCollector(tracker, other, factory, clock,
                new AgentMetricsCollector.Settings(true, 60, 60, 15, 2), id -> true).tick();
        assertEquals(List.of(90d), other.snapshot("1").agents().get(0).metrics().get(AgentMetric.CPU));
    }

    @Test
    public void actsOnlyOnJobsThisControllerOwns() {
        job("1", agent("i-mine", VMRegion.US_WEST_2));
        job("2", agent("i-theirs", VMRegion.US_EAST));

        collector(id -> id.equals("1")).tick();

        verify(west).getMetricData(any(GetMetricDataRequest.class));
        verifyNoInteractions(east);
        assertNull(store.snapshot("2"));
    }

    @Test
    public void anIdleControllerMakesNoAwsCalls() {
        collector(id -> true).tick();

        verifyNoInteractions(factory);
        verifyNoInteractions(west, east);
    }

    @Test
    public void agentsThatAreNotRunningAreNotQueried() {
        job("1", status("i-gone", VMRegion.US_WEST_2, VMImageType.AGENT, VMStatus.terminated),
                status("i-ctl", VMRegion.US_WEST_2, VMImageType.CONTROLLER, VMStatus.running));

        collector(id -> true).tick();

        verifyNoInteractions(west);
    }

    @Test
    public void aFailingRegionFlagsItsJobsStaleKeepsTheOldFiguresAndBacksOff() {
        job("1", agent("i-1", VMRegion.US_WEST_2));
        AgentMetricsCollector collector = collector(id -> true);
        collector.tick();
        when(west.getMetricData(any(GetMetricDataRequest.class))).thenThrow(CloudWatchException.builder().message("Rate exceeded").build());

        clock.set(NOW.plusSeconds(60));
        collector.tick();

        AgentMetricsSnapshot snapshot = store.snapshot("1");
        assertTrue(snapshot.stale());
        assertEquals(List.of(SAMPLE), snapshot.times(), "the last figures stay readable");
        assertEquals(NOW, snapshot.generatedAt(), "and say when they were read");

        // inside the backoff nothing is called
        clock.set(NOW.plusSeconds(70));
        collector.tick();
        verify(west, times(2)).getMetricData(any(GetMetricDataRequest.class));
        assertTrue(store.snapshot("1").stale());

        // after it, and once CloudWatch answers, the job is fresh again
        when(west.getMetricData(any(GetMetricDataRequest.class))).thenAnswer(i -> data(i.getArgument(0), 55));
        clock.set(NOW.plusSeconds(60 + 121));
        collector.tick();
        assertFalse(store.snapshot("1").stale());
    }

    @Test
    public void oneFailingRegionDoesNotStopTheOthers() {
        when(west.getMetricData(any(GetMetricDataRequest.class))).thenThrow(CloudWatchException.builder().message("boom").build());
        job("1", agent("i-w", VMRegion.US_WEST_2), agent("i-e", VMRegion.US_EAST));

        collector(id -> true).tick();

        verify(east).getMetricData(any(GetMetricDataRequest.class));
        assertEquals(List.of(55d), store.snapshot("1").agents().stream()
                .filter(a -> a.instanceId().equals("i-e")).findFirst().orElseThrow().metrics().get(AgentMetric.CPU));
        assertTrue(store.snapshot("1").stale(), "a job with an unread region is stale");
    }

    @Test
    public void aJobWithNoRunningAgentsIsEvictedAfterItsGracePeriod() {
        CloudVmStatusContainer container = job("1", agent("i-1", VMRegion.US_WEST_2));
        AgentMetricsCollector collector = collector(id -> true);
        collector.tick();
        container.getStatuses().clear();

        clock.set(NOW.plusSeconds(60));
        collector.tick();
        assertFalse(store.jobIds().isEmpty(), "readable during the grace period");

        clock.set(NOW.plusSeconds(60 + 15 * 60 + 1));
        collector.tick();
        assertTrue(store.jobIds().isEmpty());
    }

    @Test
    public void manyReadsOfTheStoreMakeNoCloudWatchCalls() {
        job("1", agent("i-1", VMRegion.US_WEST_2));
        collector(id -> true).tick();
        verify(west, times(1)).getMetricData(any(GetMetricDataRequest.class));

        for (int viewers = 0; viewers < 50; viewers++) {
            assertEquals(1, store.snapshot("1").agents().size());
        }

        verify(west, times(1)).getMetricData(any(GetMetricDataRequest.class));
        verify(west, times(1)).listMetrics(any(ListMetricsRequest.class));
    }

    // ---- fixtures

    private CloudVmStatusContainer job(String jobId, CloudVmStatus... statuses) {
        CloudVmStatusContainer container = new CloudVmStatusContainer();
        container.setJobId(jobId);
        for (CloudVmStatus s : statuses) {
            container.getStatuses().add(s);
        }
        jobs.add(container);
        return container;
    }

    private static CloudVmStatus agent(String id, VMRegion region) {
        return status(id, region, VMImageType.AGENT, VMStatus.running);
    }

    private static CloudVmStatus status(String id, VMRegion region, VMImageType role, VMStatus vm) {
        return new CloudVmStatus(id, "42", "sg", JobStatus.Running, role, region, null, vm, new ValidationStatus(), 1, 1,
                null, null);
    }

    private static Dimension dim(String name, String value) {
        return Dimension.builder().name(name).value(value).build();
    }

    private static final List<String> ALL = List.of("procstat_cpu_usage", "mem_used_percent", "jvm.memory.heap.used",
            "jvm.memory.heap.max", "jvm.threads.count", "jvm.gc.collections.elapsed", "tcp_established");

    private static ListMetricsResponse allMetrics(ListMetricsRequest request) {
        return metrics(request, ALL.toArray(new String[0]));
    }

    /** What the CloudWatch agent publishes for the instance; procstat also has a stray non-java series */
    private static ListMetricsResponse metrics(ListMetricsRequest request, String... names) {
        String id = request.dimensions().get(0).value();
        List<Metric> metrics = new ArrayList<>();
        for (String name : names) {
            if (name.startsWith("procstat")) {
                metrics.add(Metric.builder().metricName(name).dimensions(dim("InstanceId", id), dim("Service", "TankAgent"),
                        dim("exe", "bash"), dim("pid_finder", "native")).build());
                metrics.add(Metric.builder().metricName(name).dimensions(dim("InstanceId", id), dim("Service", "TankAgent"),
                        dim("exe", "java"), dim("pid_finder", "native")).build());
            } else {
                metrics.add(Metric.builder().metricName(name).dimensions(dim("InstanceId", id), dim("Service", "TankAgent")).build());
            }
        }
        return ListMetricsResponse.builder().metrics(metrics).build();
    }

    /** One sample at SAMPLE per query: cpu as given, mem 60, heap 512 MB of 2048, threads 700, gc 30, tcp 1500 */
    private static GetMetricDataResponse data(GetMetricDataRequest request, double cpu) {
        Map<String, Double> values = Map.of("procstat_cpu_usage", cpu, "mem_used_percent", 60d,
                "jvm.memory.heap.used", 512d * 1024 * 1024, "jvm.memory.heap.max", 2048d * 1024 * 1024,
                "jvm.threads.count", 700d, "jvm.gc.collections.elapsed", 30d, "tcp_established", 1500d);
        List<MetricDataResult> results = new ArrayList<>();
        for (MetricDataQuery q : request.metricDataQueries()) {
            results.add(MetricDataResult.builder().id(q.id()).timestamps(SAMPLE)
                    .values(values.get(q.metricStat().metric().metricName())).build());
        }
        return GetMetricDataResponse.builder().metricDataResults(results).build();
    }

    private static final class MutableClock extends Clock {
        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void set(Instant now) {
            this.now = now;
        }

        @Override
        public java.time.ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
