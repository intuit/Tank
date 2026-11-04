package com.intuit.tank.perfManager;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>PerfQueueTest</code> contains tests for the class <code>{@link PerfQueue}</code>.
 */
public class PerfQueueTest {

    @Test
    void executeRunsTheTaskOnAPoolThread() throws Exception {
        CountDownLatch ran = new CountDownLatch(1);
        Thread caller = Thread.currentThread();
        Thread[] worker = new Thread[1];

        new PerfQueue().execute(() -> {
            worker[0] = Thread.currentThread();
            ran.countDown();
        });

        assertTrue(ran.await(5, TimeUnit.SECONDS));
        assertNotSame(caller, worker[0]);
    }
}
