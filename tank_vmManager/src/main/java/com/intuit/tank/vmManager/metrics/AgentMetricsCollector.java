package com.intuit.tank.vmManager.metrics;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.tracing.Tracing;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vmManager.environment.amazon.CloudWatchClientFactory;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.DimensionFilter;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Reads the agents' CloudWatch figures (host, JVM and TCP, published by the CloudWatch agent baked into the agent
 * AMI) for the jobs this controller is running, once a minute, into the {@link AgentMetricsStore}. The UI reads
 * the store, never CloudWatch, so any number of viewers costs the same few calls: one {@code GetMetricData} per
 * region per 500 queries, plus a {@code ListMetrics} the first time an agent is seen to learn its exact dimension
 * sets (the CloudWatch agent adds dimensions of its own, and {@code GetMetricData} needs an exact match).
 * <p>
 * Polling starts with the application, and again on the first event that says a job has agents (see
 * {@link #startsPolling}), and keeps running; an idle tick makes no AWS call.
 * A failing region backs off exponentially, and its jobs are flagged stale while the last figures stay readable.
 */
@ApplicationScoped
public class AgentMetricsCollector {

    private static final Logger LOG = LogManager.getLogger(AgentMetricsCollector.class);

    static final String AGENT_NAMESPACE = "CWAgent";
    /** GetMetricData accepts at most 500 queries per call */
    static final int MAX_QUERIES = 500;
    static final int PERIOD_SECONDS = 60;
    /** How far back the first read of an agent goes */
    static final Duration INITIAL_WINDOW = Duration.ofMinutes(10);
    /** Overlap on later reads, so a sample that CloudWatch published late is picked up */
    static final Duration OVERLAP = Duration.ofMinutes(2);
    /** An agent missing some metrics is looked up again after this long (an old AMI never gains them) */
    static final Duration RESOLVE_RETRY = Duration.ofMinutes(3);
    static final Duration MAX_BACKOFF = Duration.ofMinutes(10);

    private static final double BYTES_PER_MB = 1024d * 1024d;

    /** What is read from CloudWatch for each agent: the metric name behind each query */
    private enum Source {
        CPU(AgentMetric.CPU, "procstat_cpu_usage"),
        MEM(AgentMetric.MEM, "mem_used_percent"),
        HEAP_USED(AgentMetric.HEAP, "jvm.memory.heap.used"),
        HEAP_MAX(null, "jvm.memory.heap.max"),
        THREADS(AgentMetric.THREADS, "jvm.threads.count"),
        GC(AgentMetric.GC, "jvm.gc.collections.elapsed"),
        TCP(AgentMetric.TCP, "netstat_tcp_established");

        final AgentMetric metric;
        final String name;

        Source(AgentMetric metric, String name) {
            this.metric = metric;
            this.name = name;
        }
    }

    /** The tunables, read from {@code agent-metrics/*} in the VM manager settings */
    public record Settings(boolean enabled, int pollSeconds, int retentionPoints, int graceMinutes, int vcpus) {
        static Settings load() {
            VmManagerConfig c = new TankConfig().getVmManagerConfig();
            return new Settings(c.isAgentMetricsEnabled(), c.getAgentMetricsPollSeconds(),
                    c.getAgentMetricsRetentionPoints(), c.getAgentMetricsGraceMinutes(), c.getAgentMetricsVcpus());
        }
    }

    @Inject
    private VMTracker vmTracker;

    @Inject
    private AgentMetricsStore store;

    private CloudWatchClientFactory clients = new CloudWatchClientFactory();
    private Clock clock = Clock.systemUTC();
    private Settings settings;
    /**
     * Whether this controller owns the job. Today the tracker only holds jobs this controller runs, so this accepts
     * all; the multi-controller work (multi-controller-plan.md, phase 2.5) replaces it with the ownership check.
     */
    private Predicate<String> ownsJob = jobId -> true;

    /** instanceId -> the dimension sets resolved for it */
    private final Map<String, Resolved> resolved = new HashMap<>();
    /** instanceId -> newest sample time read, so later reads start near it */
    private final Map<String, Instant> lastSample = new HashMap<>();
    private final Map<VMRegion, Backoff> backoffs = new HashMap<>();

    private ScheduledExecutorService scheduler;

    public AgentMetricsCollector() {
    }

    AgentMetricsCollector(VMTracker vmTracker, AgentMetricsStore store, CloudWatchClientFactory clients, Clock clock,
            Settings settings, Predicate<String> ownsJob) {
        this.vmTracker = vmTracker;
        this.store = store;
        this.clients = clients;
        this.clock = clock;
        this.settings = settings;
        this.ownsJob = ownsJob;
    }

    /**
     * Polls from startup, so jobs already running when the controller restarts are covered; the job events
     * below only start it for jobs that begin afterwards.
     */
    void onStartup(@Observes @Initialized(ApplicationScoped.class) Object ignored) {
        try {
            start();
        } catch (Exception e) {
            LOG.error("Agent metrics collector failed to start: " + e, e);
        }
    }

    public void observe(@Observes(notifyObserver = Reception.ALWAYS) JobEvent jobEvent) {
        if (startsPolling(jobEvent.getEvent())) {
            start();
        }
    }

    /**
     * The events the controller fires once a job has agents: launched, reported ready, load started.
     * AGENT_STARTED is never fired, so it can't be the trigger.
     */
    static boolean startsPolling(JobLifecycleEvent event) {
        return event == JobLifecycleEvent.AGENT_LAUNCHED
                || event == JobLifecycleEvent.AGENT_REPORTED
                || event == JobLifecycleEvent.LOAD_STARTED;
    }

    /** Starts the polling thread if metrics are enabled and it is not running; safe to call repeatedly */
    public synchronized void start() {
        if (scheduler != null) {
            return;
        }
        if (settings == null) {
            settings = Settings.load();
        }
        if (!settings.enabled()) {
            LOG.info("Agent metrics are disabled (agent-metrics/enabled)");
            return;
        }
        store.setRetention(settings.retentionPoints());
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AgentMetricsCollector");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleWithFixedDelay(this::safeTick, 0L, settings.pollSeconds(), TimeUnit.SECONDS);
        LOG.info("Agent metrics collector started, every " + settings.pollSeconds() + "s");
    }

    @PreDestroy
    public synchronized void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        clients.close();
    }

    private void safeTick() {
        try {
            tick();
        } catch (Exception e) {
            LOG.warn("Agent metrics poll failed: " + e);
        }
    }

    /** One poll of every region with running agents. Package-visible so tests drive it directly. */
    void tick() {
        Instant now = clock.instant();
        store.evict(now, Duration.ofMinutes(settings.graceMinutes()).toMillis());

        // region -> instance -> job
        Map<VMRegion, Map<String, String>> byRegion = new LinkedHashMap<>();
        Set<String> running = new HashSet<>();
        for (CloudVmStatusContainer container : vmTracker.getAllJobs()) {
            String jobId = container.getJobId();
            if (!ownsJob.test(jobId)) {
                continue;
            }
            boolean any = false;
            for (CloudVmStatus status : container.getStatuses()) {
                if (isRunningAgent(status)) {
                    any = true;
                    byRegion.computeIfAbsent(status.getVmRegion(), r -> new LinkedHashMap<>())
                            .put(status.getInstanceId(), jobId);
                }
            }
            if (any) {
                running.add(jobId);
            } else {
                store.markFinished(jobId, now);
            }
        }
        forgetGoneAgents(byRegion);
        if (!byRegion.isEmpty()) {
            Tracing.inRootSpan("Tank.AgentMetricsCollector", () -> pollRegions(byRegion, now));
        }
    }

    private void pollRegions(Map<VMRegion, Map<String, String>> byRegion, Instant now) {
        Set<String> freshJobs = new HashSet<>();
        Set<String> staleJobs = new HashSet<>();
        byRegion.forEach((region, instances) -> {
            Backoff backoff = backoffs.computeIfAbsent(region, r -> new Backoff());
            if (now.isBefore(backoff.until)) {
                staleJobs.addAll(instances.values());
                return;
            }
            try {
                readRegion(region, instances, now);
                backoff.failures = 0;
                backoff.until = Instant.EPOCH;
                freshJobs.addAll(instances.values());
            } catch (RuntimeException e) {
                backoff.failures++;
                long seconds = Math.min(MAX_BACKOFF.toSeconds(),
                        (long) settings.pollSeconds() << Math.min(backoff.failures, 6));
                backoff.until = now.plusSeconds(seconds);
                LOG.warn("Agent metrics for " + region + " failed (" + e + "); next try in " + seconds + "s");
                staleJobs.addAll(instances.values());
            }
        });
        // a job with agents in several regions is stale if any region failed
        freshJobs.removeAll(staleJobs);
        freshJobs.forEach(jobId -> store.markFresh(jobId, now));
        staleJobs.forEach(store::markStale);
    }

    private static boolean isRunningAgent(CloudVmStatus status) {
        if (status.getRole() != VMImageType.AGENT || StringUtils.isBlank(status.getInstanceId())
                || status.getVmRegion() == null) {
            return false;
        }
        VMStatus vm = status.getVmStatus();
        return vm != VMStatus.terminated && vm != VMStatus.stopped && vm != VMStatus.shutting_down
                && vm != VMStatus.replaced;
    }

    /** Drops cached state of agents no longer running, so the caches do not grow over weeks */
    private void forgetGoneAgents(Map<VMRegion, Map<String, String>> byRegion) {
        Set<String> live = new HashSet<>();
        byRegion.values().forEach(m -> live.addAll(m.keySet()));
        resolved.keySet().retainAll(live);
        lastSample.keySet().retainAll(live);
    }

    private void readRegion(VMRegion region, Map<String, String> instances, Instant now) {
        CloudWatchClient client = clients.forRegion(region);
        for (String instanceId : instances.keySet()) {
            resolve(client, instanceId, now);
        }

        // one query per (agent, source) the agent actually publishes
        List<Query> queries = new ArrayList<>();
        for (String instanceId : instances.keySet()) {
            Resolved r = resolved.get(instanceId);
            for (Source source : Source.values()) {
                List<Dimension> dims = r.dimensions.get(source);
                if (dims != null) {
                    queries.add(new Query("q" + queries.size(), instanceId, source, dims));
                }
            }
        }
        Instant start = start(instances.keySet(), now);
        for (int from = 0; from < queries.size(); from += MAX_QUERIES) {
            List<Query> chunk = queries.subList(from, Math.min(queries.size(), from + MAX_QUERIES));
            store(client, chunk, instances, start, now);
        }
    }

    private Instant start(Set<String> instanceIds, Instant now) {
        Instant start = now.minus(INITIAL_WINDOW);
        Instant earliest = null;
        for (String id : instanceIds) {
            Instant last = lastSample.get(id);
            if (last == null) {
                return start;
            }
            Instant from = last.minus(OVERLAP);
            earliest = earliest == null || from.isBefore(earliest) ? from : earliest;
        }
        return earliest != null && earliest.isAfter(start) ? earliest : start;
    }

    private void store(CloudWatchClient client, List<Query> chunk, Map<String, String> instances, Instant start,
            Instant end) {
        Map<String, Query> byId = new HashMap<>();
        List<MetricDataQuery> dataQueries = new ArrayList<>();
        for (Query q : chunk) {
            byId.put(q.id, q);
            dataQueries.add(MetricDataQuery.builder().id(q.id).returnData(true)
                    .metricStat(MetricStat.builder()
                            .metric(Metric.builder().namespace(AGENT_NAMESPACE).metricName(q.source.name)
                                    .dimensions(q.dimensions).build())
                            .period(PERIOD_SECONDS).stat("Average").build())
                    .build());
        }
        // heap percent needs used and max at the same time, so collect both before storing
        Map<String, Map<Instant, Double>> heapUsed = new HashMap<>();
        Map<String, Map<Instant, Double>> heapMax = new HashMap<>();

        String token = null;
        do {
            GetMetricDataResponse response = client.getMetricData(GetMetricDataRequest.builder()
                    .metricDataQueries(dataQueries).startTime(start).endTime(end).nextToken(token).build());
            for (MetricDataResult result : response.metricDataResults()) {
                Query q = byId.get(result.id());
                if (q == null) {
                    continue;
                }
                String instanceId = q.instanceId;
                String jobId = instances.get(instanceId);
                for (int i = 0; i < result.timestamps().size() && i < result.values().size(); i++) {
                    Instant time = result.timestamps().get(i);
                    double value = result.values().get(i);
                    lastSample.merge(instanceId, time, (a, b) -> a.isAfter(b) ? a : b);
                    switch (q.source) {
                        case HEAP_USED -> heapUsed.computeIfAbsent(instanceId, k -> new HashMap<>()).put(time, value);
                        case HEAP_MAX -> heapMax.computeIfAbsent(instanceId, k -> new HashMap<>()).put(time, value);
                        case CPU -> store.record(jobId, instanceId, AgentMetric.CPU, time, cpu(value));
                        default -> store.record(jobId, instanceId, q.source.metric, time, value);
                    }
                }
            }
            token = response.nextToken();
        } while (token != null);

        heapUsed.forEach((instanceId, used) -> {
            String jobId = instances.get(instanceId);
            Map<Instant, Double> max = heapMax.getOrDefault(instanceId, Map.of());
            used.forEach((time, value) -> {
                Double limit = max.get(time);
                if (limit != null && limit > 0) {
                    store.record(jobId, instanceId, AgentMetric.HEAP, time, Math.min(100d, 100d * value / limit));
                }
            });
        });
        heapMax.forEach((instanceId, max) -> max.entrySet().stream().max(Map.Entry.comparingByKey())
                .ifPresent(e -> store.setHeapMaxMb(instances.get(instanceId), instanceId, e.getValue() / BYTES_PER_MB)));
    }

    /** procstat_cpu_usage is percent of one core: per vCPU when the count is configured, else capped at 100 */
    private double cpu(double value) {
        int vcpus = settings.vcpus();
        return Math.min(100d, vcpus > 0 ? value / vcpus : value);
    }

    /** Finds the exact dimensions each metric of the agent is published with, once, and again if it was incomplete */
    private void resolve(CloudWatchClient client, String instanceId, Instant now) {
        Resolved known = resolved.get(instanceId);
        if (known != null && (known.complete() || now.isBefore(known.at.plus(RESOLVE_RETRY)))) {
            return;
        }
        Map<Source, List<Dimension>> found = new EnumMap<>(Source.class);
        String token = null;
        do {
            ListMetricsResponse response = client.listMetrics(ListMetricsRequest.builder()
                    .namespace(AGENT_NAMESPACE)
                    .dimensions(DimensionFilter.builder().name("InstanceId").value(instanceId).build())
                    .nextToken(token).build());
            for (Metric metric : response.metrics()) {
                for (Source source : Source.values()) {
                    if (source.name.equals(metric.metricName()) && better(metric.dimensions(), found.get(source))) {
                        found.put(source, metric.dimensions());
                    }
                }
            }
            token = response.nextToken();
        } while (token != null);
        resolved.put(instanceId, new Resolved(now, found));
    }

    /** The java process for procstat, the TankAgent service for the rest, else the first seen */
    private static boolean better(List<Dimension> candidate, List<Dimension> current) {
        return current == null || score(candidate) > score(current);
    }

    private static int score(List<Dimension> dims) {
        int score = 0;
        for (Dimension d : dims) {
            if ("exe".equals(d.name()) && "java".equals(d.value())) {
                score += 2;
            }
            if ("Service".equals(d.name()) && "TankAgent".equals(d.value())) {
                score += 1;
            }
        }
        return score;
    }

    private record Query(String id, String instanceId, Source source, List<Dimension> dimensions) {
    }

    private static final class Resolved {
        final Instant at;
        final Map<Source, List<Dimension>> dimensions;

        Resolved(Instant at, Map<Source, List<Dimension>> dimensions) {
            this.at = at;
            this.dimensions = dimensions;
        }

        boolean complete() {
            return dimensions.size() == Source.values().length;
        }
    }

    private static final class Backoff {
        int failures;
        Instant until = Instant.EPOCH;
    }
}
