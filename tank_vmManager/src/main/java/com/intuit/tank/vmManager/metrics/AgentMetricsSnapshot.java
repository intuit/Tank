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

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * What the store holds for one job, copied out so readers never see it change. Every series has one entry per
 * element of {@code times}; null where CloudWatch had no sample.
 */
public record AgentMetricsSnapshot(String jobId, Instant generatedAt, boolean stale, List<Instant> times,
        List<Agent> agents) {

    public record Agent(String instanceId, Double heapMaxMb, Map<AgentMetric, List<Double>> metrics) {
        public Agent {
            metrics = new EnumMap<>(metrics);
        }
    }
}
