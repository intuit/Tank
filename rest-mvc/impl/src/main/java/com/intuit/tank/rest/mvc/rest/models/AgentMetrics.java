/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.List;

/**
 * Host and JVM figures of a job's agents, as the controller last read them from CloudWatch. Every series has one
 * value per entry in {@code times}; a null value means CloudWatch had no sample then (an agent on an older AMI
 * has no JVM figures at all).
 *
 * @param jobId       the job
 * @param generatedAt when the controller last read CloudWatch for the job (ISO-8601, UTC)
 * @param stale       the controller could not read CloudWatch on its last try; the figures are the last it had
 * @param times       sample times, oldest first (ISO-8601, UTC)
 * @param agents      one entry per agent
 */
public record AgentMetrics(long jobId, String generatedAt, boolean stale, List<String> times, List<AgentMetricSeries> agents) {

    /**
     * @param heapMaxMb the JVM's maximum heap in megabytes, or null when not reported
     */
    public record AgentMetricSeries(String instanceId, Double heapMaxMb, AgentMetricValues metrics) {
    }

    /**
     * cpu, mem and heap are percent; threads and tcp are counts; gc is milliseconds
     */
    public record AgentMetricValues(List<Double> cpu, List<Double> mem, List<Double> heap, List<Double> threads,
                          List<Double> gc, List<Double> tcp) {
    }
}
