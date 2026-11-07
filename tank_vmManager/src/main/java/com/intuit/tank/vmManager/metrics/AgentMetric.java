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

/**
 * The figures kept per agent. {@code key} is the name the REST API and the UI use.
 */
public enum AgentMetric {
    /** CWAgent procstat_cpu_usage (java process), per vCPU when the count is configured, else capped at 100 */
    CPU("cpu"),
    /** CWAgent mem_used_percent */
    MEM("mem"),
    /** jvm.memory.heap.used as a percent of jvm.memory.heap.max */
    HEAP("heap"),
    /** CWAgent jvm.threads.count */
    THREADS("threads"),
    /** CWAgent jvm.gc.collections.elapsed, milliseconds */
    GC("gc"),
    /** CWAgent tcp_established */
    TCP("tcp");

    private final String key;

    AgentMetric(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
