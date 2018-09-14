package com.intuit.tank.job;

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

import com.intuit.tank.job.JobStatusHelper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>JobStatusHelperTest</code> contains tests for the class <code>{@link JobStatusHelper}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class JobStatusHelperTest {
    /**
     * Run the boolean canBeKilled(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCanBeKilled_1()
            throws Exception {
        String status = "";

        boolean result = JobStatusHelper.canBeKilled(status);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.job.JobStatusHelper.canBeKilled(JobStatusHelper.java:74)
        assertTrue(!result);
    }

  

    /**
     * Run the boolean canBePaused(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCanBePaused_1()
            throws Exception {
        String status = "";

        boolean result = JobStatusHelper.canBePaused(status);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.job.JobStatusHelper.canBePaused(JobStatusHelper.java:31)
        assertTrue(!result);
    }

  

    /**
     * Run the boolean canBeRun(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCanBeRun_1()
            throws Exception {
        String status = "";

        boolean result = JobStatusHelper.canBeRun(status);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.job.JobStatusHelper.canBeRun(JobStatusHelper.java:19)
        assertTrue(!result);
    }


    /**
     * Run the boolean canBeStopped(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCanBeStopped_1()
            throws Exception {
        String status = "";

        boolean result = JobStatusHelper.canBeStopped(status);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.job.JobStatusHelper.canBeStopped(JobStatusHelper.java:58)
        assertTrue(!result);
    }

    /**
     * Run the boolean canRampBePaused(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCanRampBePaused_1()
            throws Exception {
        String status = "";

        boolean result = JobStatusHelper.canRampBePaused(status);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.job.JobStatusHelper.canRampBePaused(JobStatusHelper.java:43)
        assertTrue(!result);
    }

}