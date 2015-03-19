package com.intuit.tank.project;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.project.JobValidator;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.TestPlan;

/**
 * The class <code>JobValidatorTest</code> contains tests for the class <code>{@link JobValidator}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class JobValidatorTest {
    /**
     * Run the JobValidator(Script) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testJobValidator_1()
        throws Exception {
        Script s = new Script();
        s.setName("");

        JobValidator result = new JobValidator(s);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Script.setName(Script.java:133)
        assertNotNull(result);
    }

    /**
     * Run the JobValidator(List<TestPlan>,Map<String,String>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testJobValidator_2()
        throws Exception {
        List<TestPlan> testplans = new LinkedList();
        Map<String, String> globalVariables = new HashMap();

        JobValidator result = new JobValidator(testplans, globalVariables);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:43)
        assertNotNull(result);
    }

    /**
     * Run the JobValidator(List<TestPlan>,Map<String,String>,boolean) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testJobValidator_3()
        throws Exception {
        List<TestPlan> testplans = new LinkedList();
        Map<String, String> globalVariables = new HashMap();
        boolean processAssignements = true;

        JobValidator result = new JobValidator(testplans, globalVariables, processAssignements);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the JobValidator(List<TestPlan>,Map<String,String>,boolean) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testJobValidator_4()
        throws Exception {
        List<TestPlan> testplans = new LinkedList();
        Map<String, String> globalVariables = new HashMap();
        boolean processAssignements = true;

        JobValidator result = new JobValidator(testplans, globalVariables, processAssignements);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the Map<String, Set<String>> getAssignments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetAssignments_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Map<String, Set<String>> result = fixture.getAssignments();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the Set<String> getBestPracticeViolations() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetBestPracticeViolations_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Set<String> result = fixture.getBestPracticeViolations();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the Set<String> getDataFiles() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetDataFiles_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Set<String> result = fixture.getDataFiles();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the Map<String, Set<String>> getDeclaredVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetDeclaredVariables_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Map<String, Set<String>> result = fixture.getDeclaredVariables();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the String getDuration(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetDuration_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String group = "";

        String result = fixture.getDuration(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the String getDuration(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetDuration_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String group = "";

        String result = fixture.getDuration(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the long getDurationMs(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetDurationMs_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String group = "";

        long result = fixture.getDurationMs(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertEquals(0L, result);
    }

    /**
     * Run the long getDurationMs(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetDurationMs_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String group = "";

        long result = fixture.getDurationMs(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertEquals(0L, result);
    }

    /**
     * Run the long getExpectedTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetExpectedTime_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String group = "";

        long result = fixture.getExpectedTime(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertEquals(0L, result);
    }

    /**
     * Run the long getExpectedTime(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetExpectedTime_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String group = "";

        long result = fixture.getExpectedTime(group);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertEquals(0L, result);
    }

    /**
     * Run the Set<String> getOrphanedVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetOrphanedVariables_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Set<String> result = fixture.getOrphanedVariables();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the Set<String> getSuperfluousVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetSuperfluousVariables_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Set<String> result = fixture.getSuperfluousVariables();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the Set<String> getUsedVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetUsedVariables_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        Set<String> result = fixture.getUsedVariables();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertNotNull(result);
    }

    /**
     * Run the boolean isOrphaned(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsOrphaned_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String var = "";

        boolean result = fixture.isOrphaned(var);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertTrue(!result);
    }

    /**
     * Run the boolean isOrphaned(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsOrphaned_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String var = "";

        boolean result = fixture.isOrphaned(var);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertTrue(!result);
    }

    /**
     * Run the boolean isProcessAssignements() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsProcessAssignements_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        boolean result = fixture.isProcessAssignements();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertTrue(result);
    }

    /**
     * Run the boolean isProcessAssignements() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsProcessAssignements_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), false);

        boolean result = fixture.isProcessAssignements();
        assertTrue(!result);
    }

    /**
     * Run the boolean isSuperfluous(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsSuperfluous_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String var = "";

        boolean result = fixture.isSuperfluous(var);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertTrue(!result);
    }

    /**
     * Run the boolean isSuperfluous(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsSuperfluous_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);
        String var = "";

        boolean result = fixture.isSuperfluous(var);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:54)
        assertTrue(!result);
    }

    /**
     * Run the boolean isValid() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsValid_1()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        boolean result = fixture.isValid();
        assertTrue(!result);
    }

    /**
     * Run the boolean isValid() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsValid_2()
        throws Exception {
        JobValidator fixture = new JobValidator(new LinkedList(), new HashMap(), true);

        boolean result = fixture.isValid();
        assertTrue(!result);
    }
}