package com.intuit.tank.notification;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.notification.SummaryReportObserver;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;

/**
 * The class <code>SummaryReportObserverTest</code> contains tests for the class <code>{@link SummaryReportObserver}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class SummaryReportObserverTest {
    /**
     * Run the SummaryReportObserver() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSummaryReportObserver_1()
        throws Exception {
        SummaryReportObserver result = new SummaryReportObserver();
        assertNotNull(result);
    }

    /**
     * Run the void observerJobEvents(JobEvent) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testObserverJobEvents_1()
        throws Exception {
        SummaryReportObserver fixture = new SummaryReportObserver();
        JobEvent jobEvent = new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU);

        fixture.observerJobEvents(jobEvent);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.enumerated.JobLifecycleEvent.<init>(JobLifecycleEvent.java:43)
        //       at com.intuit.tank.api.enumerated.JobLifecycleEvent.<clinit>(JobLifecycleEvent.java:14)
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }

    /**
     * Run the void observerJobEvents(JobEvent) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testObserverJobEvents_2()
        throws Exception {
        SummaryReportObserver fixture = new SummaryReportObserver();
        JobEvent jobEvent = new JobEvent("", "", JobLifecycleEvent.AGENT_EXCESSIVE_CPU);

        fixture.observerJobEvents(jobEvent);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.JobLifecycleEvent
        //       at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }
}