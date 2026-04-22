package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.project.*;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;
import com.intuit.tank.vm.settings.VmManagerConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JobDetailFormatterTest {

    // =====================================================================
    // estimateCost
    // =====================================================================

    @Test
    void estimateCost_calculatesCorrectly() {
        // 2 instances, $1.00/hr, 2 hours (7200000ms)
        BigDecimal result = JobDetailFormatter.estimateCost(2, new BigDecimal("1.00"), 7200000L);
        // 2 * 1.00 * 2 = 4.00, plus 1.5x = 4.00 + 6.00 = 10.00
        assertEquals(new BigDecimal("10.000"), result.setScale(3));
    }

    @Test
    void estimateCost_minimumOneHour() {
        // Even with 0 time, minimum is 1 hour
        BigDecimal result = JobDetailFormatter.estimateCost(1, new BigDecimal("0.50"), 0L);
        // 1 * 0.50 * 1 = 0.50, plus 1.5x = 0.50 + 0.75 = 1.25
        assertEquals(0, result.compareTo(new BigDecimal("1.25")));
    }

    @Test
    void estimateCost_zeroInstances() {
        BigDecimal result = JobDetailFormatter.estimateCost(0, new BigDecimal("1.00"), 3600000L);
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    // =====================================================================
    // addProperty
    // =====================================================================

    @Test
    void addProperty_addsKeyAndValue() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addProperty(sb, "Name", "TestJob");
        String result = sb.toString();
        assertTrue(result.contains("Name"));
        assertTrue(result.contains("TestJob"));
        assertTrue(result.contains("<br/>"));
    }

    @Test
    void addProperty_handlesNullValue() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addProperty(sb, "Label", null);
        String result = sb.toString();
        assertTrue(result.contains("Label"));
        assertTrue(result.contains("<br/>"));
    }

    @Test
    void addProperty_replacesSpacesWithNbsp() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addProperty(sb, "My Key", "val");
        assertTrue(sb.toString().contains("My&nbsp;Key"));
    }

    // =====================================================================
    // addError
    // =====================================================================

    @Test
    void addError_formatsWithErrorSpan() {
        StringBuilder sb = new StringBuilder();
        JobDetailFormatter.addError(sb, "Something went wrong");
        String result = sb.toString();
        assertTrue(result.contains("class=\"error\""));
        assertTrue(result.contains("Something went wrong"));
        assertTrue(result.contains("</span>"));
    }

    // =====================================================================
    // getSimulationTime
    // =====================================================================

    @Test
    void getSimulationTime_returnsJobSimulationTime_whenNotScriptPolicy() {
        JobInstance job = new JobInstance();
        job.setSimulationTime(60000L);
        job.setTerminationPolicy(TerminationPolicy.time);

        Workload workload = new Workload();
        JobValidator validator = mock(JobValidator.class);

        long result = JobDetailFormatter.getSimulationTime(job, workload, validator);
        assertEquals(60000L, result);
    }

    @Test
    void getSimulationTime_calculatesFromTestPlans_whenScriptPolicy() {
        JobInstance job = new JobInstance();
        job.setSimulationTime(0L);
        job.setTerminationPolicy(TerminationPolicy.script);

        TestPlan tp1 = TestPlan.builder().name("Plan1").usersPercentage(50).build();
        TestPlan tp2 = TestPlan.builder().name("Plan2").usersPercentage(50).build();
        Workload workload = new Workload();
        workload.addTestPlan(tp1);
        workload.addTestPlan(tp2);

        JobValidator validator = mock(JobValidator.class);
        when(validator.getExpectedTime("Plan1")).thenReturn(30000L);
        when(validator.getExpectedTime("Plan2")).thenReturn(50000L);

        long result = JobDetailFormatter.getSimulationTime(job, workload, validator);
        assertEquals(50000L, result); // max of the two
    }

    // =====================================================================
    // createJobDetails with scriptName (simpler path)
    // =====================================================================

    // =====================================================================
    // createJobDetails with proposedJobInstance - increasing workload
    // =====================================================================

    @Test
    void createJobDetails_withJobInstance_increasingWorkload_returnsFullDetails() {
        JobValidator validator = mock(JobValidator.class);
        when(validator.getDeclaredVariables()).thenReturn(Collections.emptyMap());
        when(validator.getUsedVariables()).thenReturn(Collections.emptySet());
        when(validator.getBestPracticeViolations()).thenReturn(Collections.emptySet());
        when(validator.getAssignments()).thenReturn(Collections.emptyMap());
        when(validator.isProcessAssignements()).thenReturn(false);
        when(validator.getExpectedTime("MainPlan")).thenReturn(60000L);

        Script script = new Script();
        script.setId(1);
        script.setName("TestScript");

        ScriptGroupStep sgs = new ScriptGroupStep();
        sgs.setScript(script);
        sgs.setLoop(1);

        ScriptGroup sg = new ScriptGroup();
        sg.setName("Group1");
        sg.setLoop(1);
        sg.addScriptGroupStep(sgs);

        TestPlan tp = TestPlan.builder().name("MainPlan").usersPercentage(100).build();
        tp.addScriptGroup(sg);

        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        workload.addTestPlan(tp);

        JobInstance job = new JobInstance();
        job.setName("TestJob");
        job.setCreator("admin");
        job.setIncrementStrategy(IncrementStrategy.increasing);
        job.setTerminationPolicy(TerminationPolicy.script);
        job.setTotalVirtualUsers(100);
        job.setNumUsersPerAgent(50);
        job.setRampTime(10000L);
        job.setSimulationTime(60000L);
        job.setLocation("us-west-2");
        job.setLoggingProfile("STANDARD");
        job.setStopBehavior("END_OF_SCRIPT_GROUP");
        job.setVmInstanceType("m5.large");
        job.setUserIntervalIncrement(10);
        job.setExecutionTime(60000L);

        // Add a job region version
        JobRegion region = new JobRegion(VMRegion.US_WEST_2, "100", "100");
        region.setId(1);
        job.getJobRegionVersions().add(new EntityVersion(1, 0, JobRegion.class));

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientName(anyString())).thenReturn("ApacheHttpClient");
        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        VmInstanceType vmType = mock(VmInstanceType.class);
        when(vmType.getName()).thenReturn("m5.large");
        when(vmType.getCost()).thenReturn(0.096);
        when(vmType.getCpus()).thenReturn(4);
        when(vmType.getEcus()).thenReturn(8);
        when(vmType.getMemory()).thenReturn(8.0);
        when(mockVmConfig.getInstanceTypes()).thenReturn(List.of(vmType));

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> {
                    when(mock.getAgentConfig()).thenReturn(mockAgentConfig);
                    when(mock.getVmManagerConfig()).thenReturn(mockVmConfig);
                    when(mock.getStandalone()).thenReturn(false);
                });
             MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(region));
             MockedConstruction<DataFileDao> dfdMock = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> jndMock = Mockito.mockConstruction(JobNotificationDao.class)) {

            String result = JobDetailFormatter.createJobDetails(validator, workload, job);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.contains("TestJob"), "Expected 'TestJob' in: " + result.substring(0, Math.min(500, result.length())));
            assertTrue(result.contains("admin"));
            assertTrue(result.contains("Linear"));
            assertTrue(result.contains("Variable&nbsp;Validation"));
        }
    }

    @Test
    void createJobDetails_withJobInstance_blankName_showsError() {
        JobValidator validator = mock(JobValidator.class);
        when(validator.getDeclaredVariables()).thenReturn(Collections.emptyMap());
        when(validator.getUsedVariables()).thenReturn(Collections.emptySet());
        when(validator.getBestPracticeViolations()).thenReturn(Collections.emptySet());
        when(validator.getAssignments()).thenReturn(Collections.emptyMap());
        when(validator.isProcessAssignements()).thenReturn(false);
        when(validator.getExpectedTime(anyString())).thenReturn(0L);

        TestPlan tp = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        workload.addTestPlan(tp);

        JobInstance job = new JobInstance();
        job.setName(""); // blank name triggers error
        job.setIncrementStrategy(IncrementStrategy.increasing);
        job.setTerminationPolicy(TerminationPolicy.time);
        job.setSimulationTime(0L); // 0 + time termination triggers error
        job.setExecutionTime(0L);
        job.setVmInstanceType("m5.large");
        job.setStopBehavior("END_OF_SCRIPT_GROUP");
        job.setLoggingProfile("STANDARD");

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientName(anyString())).thenReturn("Client");
        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        when(mockVmConfig.getInstanceTypes()).thenReturn(Collections.emptyList());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> {
                    when(mock.getAgentConfig()).thenReturn(mockAgentConfig);
                    when(mock.getVmManagerConfig()).thenReturn(mockVmConfig);
                    when(mock.getStandalone()).thenReturn(false);
                });
             MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class);
             MockedConstruction<DataFileDao> dfdMock = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> jndMock = Mockito.mockConstruction(JobNotificationDao.class)) {

            String result = JobDetailFormatter.createJobDetails(validator, workload, job);

            assertNotNull(result);
            assertTrue(result.contains("ERRORS"));
            assertTrue(result.contains("Name cannot be null"));
            assertTrue(result.contains("Simulation time not set"));
            assertTrue(result.contains("No users defined"));
            assertTrue(result.contains("No scripts defined"));
        }
    }

    @Test
    void createJobDetails_withJobInstance_standardWorkload_showsRegions() {
        JobValidator validator = mock(JobValidator.class);
        when(validator.getDeclaredVariables()).thenReturn(Collections.emptyMap());
        when(validator.getUsedVariables()).thenReturn(Collections.emptySet());
        when(validator.getBestPracticeViolations()).thenReturn(Collections.emptySet());
        when(validator.getAssignments()).thenReturn(Collections.emptyMap());
        when(validator.isProcessAssignements()).thenReturn(false);
        when(validator.getExpectedTime(anyString())).thenReturn(0L);

        TestPlan tp = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        workload.addTestPlan(tp);

        JobInstance job = new JobInstance();
        job.setName("NonlinearJob");
        job.setCreator("admin");
        job.setIncrementStrategy(IncrementStrategy.standard);
        job.setTerminationPolicy(TerminationPolicy.script);
        job.setTargetRampRate(10.0);
        job.setTargetRatePerAgent(5.0);
        job.setNumAgents(2);
        job.setRampTime(5000L);
        job.setSimulationTime(30000L);
        job.setExecutionTime(30000L);
        job.setVmInstanceType("m5.large");
        job.setStopBehavior("END_OF_SCRIPT_GROUP");
        job.setLoggingProfile("STANDARD");
        job.setLocation("us-west-2");

        JobRegion region = new JobRegion(VMRegion.US_WEST_2, "0", "100");
        region.setId(1);
        job.getJobRegionVersions().add(new EntityVersion(1, 0, JobRegion.class));

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientName(anyString())).thenReturn("Client");
        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        when(mockVmConfig.getInstanceTypes()).thenReturn(Collections.emptyList());
        when(mockVmConfig.getRegions()).thenReturn(Collections.emptySet());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> {
                    when(mock.getAgentConfig()).thenReturn(mockAgentConfig);
                    when(mock.getVmManagerConfig()).thenReturn(mockVmConfig);
                    when(mock.getStandalone()).thenReturn(false);
                });
             MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(region));
             MockedConstruction<DataFileDao> dfdMock = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> jndMock = Mockito.mockConstruction(JobNotificationDao.class)) {

            String result = JobDetailFormatter.createJobDetails(validator, workload, job);

            assertNotNull(result);
            assertTrue(result.contains("NonlinearJob"));
            assertTrue(result.contains("Nonlinear"));
            assertTrue(result.contains("Regions"));
        }
    }

    @Test
    void createJobDetails_withVariablesAndDataFiles() {
        JobValidator validator = mock(JobValidator.class);
        Map<String, Set<String>> declaredVars = new HashMap<>();
        declaredVars.put("server", Set.of("project:: example.com"));
        when(validator.getDeclaredVariables()).thenReturn(declaredVars);
        when(validator.getUsedVariables()).thenReturn(Set.of("server", "orphanedVar"));
        when(validator.isOrphaned("orphanedVar")).thenReturn(true);
        when(validator.isSuperfluous("server")).thenReturn(false);
        when(validator.getBestPracticeViolations()).thenReturn(Set.of("No Logging Keys"));
        when(validator.getAssignments()).thenReturn(Collections.emptyMap());
        when(validator.isProcessAssignements()).thenReturn(true);
        when(validator.getExpectedTime(anyString())).thenReturn(0L);

        TestPlan tp = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        workload.addTestPlan(tp);

        JobInstance job = new JobInstance();
        job.setName("VarJob");
        job.setIncrementStrategy(IncrementStrategy.increasing);
        job.setTerminationPolicy(TerminationPolicy.script);
        job.setTotalVirtualUsers(10);
        job.setNumUsersPerAgent(10);
        job.setExecutionTime(0L);
        job.setVmInstanceType("m5.large");
        job.setStopBehavior("END_OF_SCRIPT_GROUP");
        job.setLoggingProfile("STANDARD");
        job.getVariables().put("server", "example.com");
        job.getVariables().put("dataFile", "users.csv");

        // Add data file version
        DataFile df = new DataFile();
        df.setId(5);
        df.setPath("users.csv");
        job.getDataFileVersions().add(new EntityVersion(5, 0, DataFile.class));

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientName(anyString())).thenReturn("Client");
        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        when(mockVmConfig.getInstanceTypes()).thenReturn(Collections.emptyList());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> {
                    when(mock.getAgentConfig()).thenReturn(mockAgentConfig);
                    when(mock.getVmManagerConfig()).thenReturn(mockVmConfig);
                    when(mock.getStandalone()).thenReturn(false);
                });
             MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class);
             MockedConstruction<DataFileDao> dfdMock = Mockito.mockConstruction(DataFileDao.class,
                (mock, ctx) -> when(mock.findById(5)).thenReturn(df));
             MockedConstruction<JobNotificationDao> jndMock = Mockito.mockConstruction(JobNotificationDao.class)) {

            String result = JobDetailFormatter.createJobDetails(validator, workload, job);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.contains("Data&nbsp;Files"));
            assertTrue(result.contains("users.csv"));
            assertTrue(result.contains("Global&nbsp;Variables"));
            assertTrue(result.contains("Variable&nbsp;Validation"));
            assertTrue(result.contains("Best&nbsp;Practice&nbsp;Violations"));
        }
    }

    @Test
    void createJobDetails_withStandaloneConfig_showsUsersInsteadOfRegion() {
        JobValidator validator = mock(JobValidator.class);
        when(validator.getDeclaredVariables()).thenReturn(Collections.emptyMap());
        when(validator.getUsedVariables()).thenReturn(Collections.emptySet());
        when(validator.getBestPracticeViolations()).thenReturn(Collections.emptySet());
        when(validator.getAssignments()).thenReturn(Collections.emptyMap());
        when(validator.isProcessAssignements()).thenReturn(false);
        when(validator.getExpectedTime(anyString())).thenReturn(0L);

        TestPlan tp = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        workload.addTestPlan(tp);

        JobInstance job = new JobInstance();
        job.setName("StandaloneJob");
        job.setIncrementStrategy(IncrementStrategy.increasing);
        job.setTerminationPolicy(TerminationPolicy.script);
        job.setTotalVirtualUsers(50);
        job.setNumUsersPerAgent(50);
        job.setExecutionTime(0L);
        job.setVmInstanceType("m5.large");
        job.setStopBehavior("END_OF_SCRIPT_GROUP");
        job.setLoggingProfile("STANDARD");

        JobRegion region = new JobRegion(VMRegion.US_WEST_2, "50", "100");
        region.setId(1);
        job.getJobRegionVersions().add(new EntityVersion(1, 0, JobRegion.class));

        AgentConfig mockAgentConfig = mock(AgentConfig.class);
        when(mockAgentConfig.getTankClientName(anyString())).thenReturn("Client");
        VmManagerConfig mockVmConfig = mock(VmManagerConfig.class);
        when(mockVmConfig.getInstanceTypes()).thenReturn(Collections.emptyList());

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> {
                    when(mock.getAgentConfig()).thenReturn(mockAgentConfig);
                    when(mock.getVmManagerConfig()).thenReturn(mockVmConfig);
                    when(mock.getStandalone()).thenReturn(true); // standalone mode
                });
             MockedConstruction<JobRegionDao> jrdMock = Mockito.mockConstruction(JobRegionDao.class,
                (mock, ctx) -> when(mock.findById(1)).thenReturn(region));
             MockedConstruction<DataFileDao> dfdMock = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> jndMock = Mockito.mockConstruction(JobNotificationDao.class)) {

            String result = JobDetailFormatter.createJobDetails(validator, workload, job);

            assertNotNull(result);
            assertTrue(result.contains("Users")); // Standalone shows "Users" instead of region name
        }
    }

    @Test
    void createJobDetails_withScriptName_returnsDetails() {
        JobValidator validator = mock(JobValidator.class);
        when(validator.getDuration("MyScript")).thenReturn("00:01:30");
        when(validator.getDeclaredVariables()).thenReturn(java.util.Collections.emptyMap());
        when(validator.getUsedVariables()).thenReturn(java.util.Collections.emptySet());
        when(validator.getBestPracticeViolations()).thenReturn(java.util.Collections.emptySet());
        when(validator.getAssignments()).thenReturn(java.util.Collections.emptyMap());
        when(validator.isProcessAssignements()).thenReturn(false);

        try (MockedConstruction<TankConfig> tankConfigMock = Mockito.mockConstruction(TankConfig.class)) {
            String result = JobDetailFormatter.createJobDetails(validator, "MyScript");
            assertNotNull(result);
            assertTrue(result.contains("MyScript"));
            assertTrue(result.contains("Estimated Time"));
        }
    }
}
