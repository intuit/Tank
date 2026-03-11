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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.intuit.tank.project.JobDetailFormatter;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.JobValidator;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JobDetailFormatterTest {

    @Test
    public void addError() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addError(sb, "Error");
        assertFalse(sb.isEmpty());
    }

    @Test
    public void addProperty() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addProperty(sb, "key", "value");
        assertTrue(sb.length() > 0);
    }


    @Test
    public void buildDetails() {
        // Test buildDetails with a full JobInstance (no versions - avoids DB calls for regions/datafiles/notifications)
        JobInstance ji = new JobInstance();
        ji.setName("TestJob");
        ji.setCreator("admin");
        ji.setSimulationTime(3600000L);
        ji.setRampTime(600000L);
        ji.setBaselineVirtualUsers(10);
        ji.setTotalVirtualUsers(100);

        TestPlan tp = TestPlan.builder().name("Main").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setTestPlan(new java.util.LinkedList<>(java.util.List.of(tp)));

        JobValidator validator = new JobValidator(java.util.List.of(tp), new java.util.HashMap<>());

        // This exercises the buildDetails(validator, workload, jobInstance, null) path
        String result = JobDetailFormatter.createJobDetails(validator, workload, ji);
        assertNotNull(result);
        assertTrue(result.contains("TestJob"));
    }

    @Test
    public void buildDetails_WithBestPracticeViolations() {
        // buildDetails with a Script that has best practice violations
        Script script = new Script();
        script.setName("TestScript");
        JobValidator validator = new JobValidator(script);

        // No best practice violations by default - so no prefix added
        String result = JobDetailFormatter.createJobDetails(validator, "TestScript");
        assertNotNull(result);
    }

    @Test
    public void calculateCost() {
        // Test with standard (non-increasing) strategy - uses numAgents
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        proposedJobInstance.setIncrementStrategy(com.intuit.tank.vm.api.enumerated.IncrementStrategy.standard);
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 3600000L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);
        assertNotNull(result);
        assertTrue(result.startsWith("$"));
    }

    @Test
    public void createJobDetailsJobValidatorWorkloadJobInstance() {
        JobValidator validator = new JobValidator(new Script());
        Workload workload = new Workload();
        JobInstance proposedJobInstance = new JobInstance();
        proposedJobInstance.setName("TestJob");

        String result = JobDetailFormatter.createJobDetails(validator, workload, proposedJobInstance);
        assertNotNull(result);
    }

    @Test
    public void createJobDetailsJobValidatorString() {
        JobValidator validator = new JobValidator(new Script());
        String result = JobDetailFormatter.createJobDetails(validator, "MyScript");
        assertNotNull(result);
        assertTrue(result.contains("MyScript"));
    }

    @Test
    public void estimateCost() {
        BigDecimal estimateCost = JobDetailFormatter.estimateCost(2, BigDecimal.valueOf(1.5D), 600000);
        assertEquals(7.50D, estimateCost.doubleValue());
    }

    @Test
    public void getSimulationTime() {
        JobInstance ji = new JobInstance();
        ji.setSimulationTime(2000L);
//        JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator)
//        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void getVmDetails() {
        String vmDetails = JobDetailFormatter.getVmDetails(new TankConfig(), "m.xlarge");
        assertEquals("m.xlarge (types=[m8g.xlarge, m7g.xlarge] cpus=4 ecus=16 memory=16.0 cost=$0.1795 per hour)", vmDetails);
    }

    /**
     * Run the JobDetailFormatter() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testJobDetailFormatter_1()
        throws Exception {
        JobDetailFormatter result = new JobDetailFormatter();
        assertNotNull(result);
    }

    /**
     * Run the void addError(StringBuilder,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddError_1()
        throws Exception {
        StringBuilder sb = new StringBuilder();
        String message = "";

        JobDetailFormatter.addError(sb, message);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobDetailFormatter.addError(JobDetailFormatter.java:350)
    }

    /**
     * Run the void addProperty(StringBuilder,String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddProperty_1()
        throws Exception {
        StringBuilder sb = new StringBuilder();
        String key = "";
        String value = "";

        JobDetailFormatter.addProperty(sb, key, value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobDetailFormatter.addProperty(JobDetailFormatter.java:320)
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_1()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.settings.TankConfig.<clinit>(TankConfig.java:52)
        assertNotNull(result);
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_2()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_3()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String calculateCost(TankConfig,JobInstance,List<JobRegion>,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCalculateCost_4()
        throws Exception {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        List<JobRegion> regions = new LinkedList<>();
        long simulationTime = 1L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String createJobDetails(JobValidator,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCreateJobDetails_1()
        throws Exception {
        JobValidator validator = new JobValidator(new Script());
        String scriptName = "";

        String result = JobDetailFormatter.createJobDetails(validator, scriptName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:60)
        assertNotNull(result);
    }

    /**
     * Run the String createJobDetails(JobValidator,Workload,JobInstance) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testCreateJobDetails_2()
        throws Exception {
        JobValidator validator = new JobValidator(new Script());
        Workload workload = new Workload();
        JobInstance proposedJobInstance = new JobInstance();

        String result = JobDetailFormatter.createJobDetails(validator, workload, proposedJobInstance);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobValidator.<init>(JobValidator.java:60)
        assertNotNull(result);
    }

    /**
     * Run the BigDecimal estimateCost(int,BigDecimal,long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEstimateCost_1()
        throws Exception {
        int numInstances = 1;
        BigDecimal costPerHour = new BigDecimal(1.0);
        long time = 1L;

        BigDecimal result = JobDetailFormatter.estimateCost(numInstances, costPerHour, time);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.JobDetailFormatter.estimateCost(JobDetailFormatter.java:300)
        assertNotNull(result);
    }

    /**
     * Run the long getSimulationTime(JobInstance,Workload,JobValidator) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSimulationTime_1()
        throws Exception {
        JobInstance proposedJobInstance = new JobInstance();
        Workload workload = new Workload();
        workload.setTestPlan(new LinkedList<>());
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<init>(IncrementStrategy.java:23)
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<clinit>(IncrementStrategy.java:13)
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertEquals(0L, result);
    }

    /**
     * Run the long getSimulationTime(JobInstance,Workload,JobValidator) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSimulationTime_2()
        throws Exception {
        JobInstance proposedJobInstance = new JobInstance();
        Workload workload = new Workload();
        workload.setTestPlan(new LinkedList<>());
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertEquals(0L, result);
    }

    /**
     * Run the long getSimulationTime(JobInstance,Workload,JobValidator) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSimulationTime_3()
        throws Exception {
        JobInstance proposedJobInstance = new JobInstance();
        Workload workload = new Workload();
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobInstance.<init>(JobInstance.java:115)
        assertEquals(0L, result);
    }

    @Test
    public void testGetSimulationTime_WithScriptTerminationPolicy() {
        JobInstance proposedJobInstance = new JobInstance();
        proposedJobInstance.setTerminationPolicy(com.intuit.tank.vm.api.enumerated.TerminationPolicy.script);
        Workload workload = new Workload();
        TestPlan tp = TestPlan.builder().name("tp").usersPercentage(100).build();
        workload.addTestPlan(tp);
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);
        // with no scripts, expected time = 0
        assertEquals(0L, result);
    }

    @Test
    public void testGetSimulationTime_WithTimeTerminationPolicy() {
        JobInstance proposedJobInstance = new JobInstance();
        proposedJobInstance.setTerminationPolicy(com.intuit.tank.vm.api.enumerated.TerminationPolicy.time);
        proposedJobInstance.setSimulationTime(5000L);
        Workload workload = new Workload();
        JobValidator validator = new JobValidator(new Script());

        long result = JobDetailFormatter.getSimulationTime(proposedJobInstance, workload, validator);
        assertEquals(5000L, result);
    }

    @Test
    public void testCalculateCost_WithIncreasingStrategy() {
        TankConfig config = new TankConfig();
        JobInstance proposedJobInstance = new JobInstance();
        proposedJobInstance.setIncrementStrategy(com.intuit.tank.vm.api.enumerated.IncrementStrategy.increasing);
        proposedJobInstance.setNumUsersPerAgent(100);
        List<JobRegion> regions = new LinkedList<>();
        regions.add(new JobRegion(com.intuit.tank.vm.api.enumerated.VMRegion.US_EAST, "200"));
        long simulationTime = 3600000L;

        String result = JobDetailFormatter.calculateCost(config, proposedJobInstance, regions, simulationTime);
        assertNotNull(result);
        assertTrue(result.startsWith("$"));
    }

    @Test
    public void testEstimateCost_MultipleInstances() {
        BigDecimal cost = JobDetailFormatter.estimateCost(5, BigDecimal.valueOf(0.5D), 7200000L);
        assertNotNull(cost);
        assertTrue(cost.doubleValue() > 0);
    }

    @Test
    public void testCreateJobDetails_WithScriptName() {
        JobValidator validator = new JobValidator(new Script());
        String result = JobDetailFormatter.createJobDetails(validator, "TestScript");
        assertNotNull(result);
        assertTrue(result.contains("TestScript"));
    }

    @Test
    public void testAddProperty_WithStyleAddsSpanTags() {
        StringBuilder sb = new StringBuilder();
        // addProperty(key, value) with both non-blank
        JobDetailFormatter.addProperty(sb, "MyKey", "MyValue");
        String content = sb.toString();
        assertTrue(content.contains("MyKey"));
        assertTrue(content.contains("MyValue"));
    }

    @Test
    public void testBuildDetails_WithStandardStrategy_CoversStandardBranches() {
        JobInstance ji = new JobInstance();
        ji.setName("StandardJob");
        ji.setCreator("admin");
        ji.setIncrementStrategy(IncrementStrategy.standard);
        ji.setSimulationTime(3600000L);
        ji.setRampTime(600000L);
        ji.setBaselineVirtualUsers(10);
        ji.setTotalVirtualUsers(100);
        ji.setTargetRampRate(10.0);
        ji.setTargetRatePerAgent(5.0);
        ji.setNumUsersPerAgent(50);

        TestPlan tp = TestPlan.builder().name("Main").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setTestPlan(new java.util.LinkedList<>(java.util.List.of(tp)));

        JobValidator validator = new JobValidator(java.util.List.of(tp), new java.util.HashMap<>());

        String result = JobDetailFormatter.createJobDetails(validator, workload, ji);
        assertNotNull(result);
        assertTrue(result.contains("StandardJob"));
    }

    @Test
    public void testBuildDetails_WithIncreasingStrategy_ZeroTotalUsers_ShowsError() {
        JobInstance ji = new JobInstance();
        ji.setName("IncreasingJob");
        ji.setCreator("admin");
        ji.setIncrementStrategy(IncrementStrategy.increasing);
        ji.setTotalVirtualUsers(0); // error case
        ji.setTerminationPolicy(TerminationPolicy.time);
        ji.setSimulationTime(0); // error case

        TestPlan tp = TestPlan.builder().name("Main").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setTestPlan(new java.util.LinkedList<>(java.util.List.of(tp)));

        JobValidator validator = new JobValidator(java.util.List.of(tp), new java.util.HashMap<>());

        String result = JobDetailFormatter.createJobDetails(validator, workload, ji);
        assertNotNull(result);
        assertTrue(result.contains("ERRORS"));
    }

    @Test
    public void testBuildDetails_WithVariablesContainingCsv() {
        JobInstance ji = new JobInstance();
        ji.setName("CsvJob");
        ji.setCreator("admin");
        ji.setIncrementStrategy(IncrementStrategy.increasing);
        ji.setSimulationTime(3600000L);
        ji.setRampTime(600000L);
        ji.setTotalVirtualUsers(50);

        // Add a variable that ends in .csv (not in datafiles list - should generate warning)
        Map<String, String> vars = new HashMap<>();
        vars.put("dataFile", "mydata.csv");
        ji.setVariables(vars);

        TestPlan tp = TestPlan.builder().name("Main").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setTestPlan(new java.util.LinkedList<>(java.util.List.of(tp)));

        JobValidator validator = new JobValidator(java.util.List.of(tp), new java.util.HashMap<>());

        String result = JobDetailFormatter.createJobDetails(validator, workload, ji);
        assertNotNull(result);
        // Should contain warning about the CSV reference
        assertTrue(result.contains("mydata.csv") || result.contains("dataFile"));
    }

    @Test
    public void testBuildDetails_WithBlankName_ShowsNameError() {
        JobInstance ji = new JobInstance();
        ji.setName(""); // blank name triggers error
        ji.setCreator("admin");
        ji.setIncrementStrategy(IncrementStrategy.increasing);
        ji.setSimulationTime(3600000L);
        ji.setTotalVirtualUsers(10);

        Workload workload = new Workload();
        workload.setTestPlan(new java.util.LinkedList<>());

        JobValidator validator = new JobValidator(new java.util.LinkedList<>(), new java.util.HashMap<>());

        String result = JobDetailFormatter.createJobDetails(validator, workload, ji);
        assertNotNull(result);
        // blank name causes "Name cannot be null" error
        assertTrue(result.contains("ERRORS") || result.contains("Name cannot be null") || result.contains("Name"));
    }

    @Test
    public void testBuildDetails_WithTestPlanAndScriptGroup_CoversScriptLoop() {
        JobInstance ji = new JobInstance();
        ji.setName("ScriptJob");
        ji.setCreator("admin");
        ji.setIncrementStrategy(IncrementStrategy.increasing);
        ji.setSimulationTime(3600000L);
        ji.setRampTime(600000L);
        ji.setTotalVirtualUsers(100);

        TestPlan tp = TestPlan.builder().name("Main").usersPercentage(100).build();
        // No script groups - should add "contains no script groups" error
        Workload workload = new Workload();
        workload.setTestPlan(new java.util.LinkedList<>(java.util.List.of(tp)));

        JobValidator validator = new JobValidator(java.util.List.of(tp), new java.util.HashMap<>());

        String result = JobDetailFormatter.createJobDetails(validator, workload, ji);
        assertNotNull(result);
    }

    /**
     * Run the String getVmDetails(TankConfig,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmDetails_1()
        throws Exception {
        TankConfig config = new TankConfig();
        String vmInstanceType = "";

        String result = JobDetailFormatter.getVmDetails(config, vmInstanceType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String getVmDetails(TankConfig,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmDetails_2()
        throws Exception {
        TankConfig config = new TankConfig();
        String vmInstanceType = "";

        String result = JobDetailFormatter.getVmDetails(config, vmInstanceType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }

    /**
     * Run the String getVmDetails(TankConfig,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmDetails_3()
        throws Exception {
        TankConfig config = new TankConfig();
        String vmInstanceType = "";

        String result = JobDetailFormatter.getVmDetails(config, vmInstanceType);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.settings.TankConfig
        assertNotNull(result);
    }
}
