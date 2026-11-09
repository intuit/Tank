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

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The CloudWatch figures the collector has read for running jobs, newest {@code retention} samples per metric
 * per agent. Readers get copies and never touch CloudWatch, so any number of viewers costs nothing.
 * A finished job's figures are dropped once its grace period has passed.
 */
@ApplicationScoped
public class AgentMetricsStore {

    static final int DEFAULT_RETENTION = 60;

    private final Map<String, JobMetrics> jobs = new ConcurrentHashMap<>();
    private volatile int retention = DEFAULT_RETENTION;

    /** @param points samples kept per metric per agent */
    public void setRetention(int points) {
        this.retention = Math.max(2, points);
    }

    /** Stores one sample. A later sample for the same agent, metric and time replaces the earlier one. */
    public void record(String jobId, String instanceId, AgentMetric metric, Instant time, double value) {
        JobMetrics job = jobs.computeIfAbsent(jobId, id -> new JobMetrics());
        synchronized (job) {
            AgentSeries series = job.agents.computeIfAbsent(instanceId, id -> new AgentSeries());
            series.samples.computeIfAbsent(metric, m -> new TreeMap<>()).put(time.toEpochMilli(), value);
            NavigableMap<Long, Double> samples = series.samples.get(metric);
            while (samples.size() > retention) {
                samples.pollFirstEntry();
            }
        }
    }

    /** Heap maximum for the agent, in megabytes */
    public void setHeapMaxMb(String jobId, String instanceId, double megabytes) {
        JobMetrics job = jobs.computeIfAbsent(jobId, id -> new JobMetrics());
        synchronized (job) {
            job.agents.computeIfAbsent(instanceId, id -> new AgentSeries()).heapMaxMb = megabytes;
        }
    }

    /** The job's agents were read successfully at {@code now}: the figures are fresh and the job is running */
    public void markFresh(String jobId, Instant now) {
        JobMetrics job = jobs.computeIfAbsent(jobId, id -> new JobMetrics());
        synchronized (job) {
            job.updated = now;
            job.stale = false;
            job.finishedAtMs = null;
        }
    }

    /** The collector could not read CloudWatch for the job: the figures stay but are flagged */
    public void markStale(String jobId) {
        JobMetrics job = jobs.get(jobId);
        if (job != null) {
            synchronized (job) {
                job.stale = true;
            }
        }
    }

    /** The job has no running agents left; its figures stay readable until the grace period ends */
    public void markFinished(String jobId, Instant now) {
        JobMetrics job = jobs.get(jobId);
        if (job != null) {
            synchronized (job) {
                if (job.finishedAtMs == null) {
                    job.finishedAtMs = now.toEpochMilli();
                }
            }
        }
    }

    /** Drops jobs that finished more than {@code graceMillis} before {@code now} */
    public void evict(Instant now, long graceMillis) {
        jobs.entrySet().removeIf(e -> {
            Long finished = e.getValue().finishedAtMs;
            return finished != null && now.toEpochMilli() - finished > graceMillis;
        });
    }

    /** Every job the store holds figures for */
    public List<String> jobIds() {
        return new ArrayList<>(jobs.keySet());
    }

    /** The job's figures, or null when the store has none for it */
    public AgentMetricsSnapshot snapshot(String jobId) {
        JobMetrics job = jobs.get(jobId);
        if (job == null) {
            return null;
        }
        synchronized (job) {
            // one shared time axis, the newest `retention` times any agent has a sample at
            TreeSet<Long> all = new TreeSet<>();
            job.agents.values().forEach(a -> a.samples.values().forEach(s -> all.addAll(s.keySet())));
            List<Long> everyTime = new ArrayList<>(all);
            final List<Long> axis = everyTime.size() > retention
                    ? everyTime.subList(everyTime.size() - retention, everyTime.size()) : everyTime;
            List<Instant> times = axis.stream().map(Instant::ofEpochMilli).toList();
            List<AgentMetricsSnapshot.Agent> agents = new ArrayList<>();
            new TreeMap<>(job.agents).forEach((instanceId, series) -> {
                Map<AgentMetric, List<Double>> metrics = new EnumMap<>(AgentMetric.class);
                for (AgentMetric metric : AgentMetric.values()) {
                    NavigableMap<Long, Double> samples = series.samples.get(metric);
                    List<Double> values = new ArrayList<>(axis.size());
                    for (Long t : axis) {
                        values.add(samples == null ? null : samples.get(t));
                    }
                    metrics.put(metric, values);
                }
                agents.add(new AgentMetricsSnapshot.Agent(instanceId, series.heapMaxMb, metrics));
            });
            return new AgentMetricsSnapshot(jobId, job.updated != null ? job.updated : Instant.EPOCH, job.stale, times,
                    agents);
        }
    }

    private static final class JobMetrics {
        final Map<String, AgentSeries> agents = new ConcurrentHashMap<>();
        Instant updated;
        boolean stale;
        Long finishedAtMs;
    }

    private static final class AgentSeries {
        final Map<AgentMetric, NavigableMap<Long, Double>> samples = new EnumMap<>(AgentMetric.class);
        Double heapMaxMb;
    }
}
