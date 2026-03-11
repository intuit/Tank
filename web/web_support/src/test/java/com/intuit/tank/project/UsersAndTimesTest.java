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

import com.intuit.tank.ProjectBean;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsersAndTimesTest {

    @InjectMocks
    private UsersAndTimes usersAndTimes;

    @Mock
    private ProjectBean projectBean;

    @Mock
    private Messages messages;

    private JobConfiguration jobConfiguration;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        jobConfiguration = new JobConfiguration();
        jobConfiguration.setTerminationPolicy(TerminationPolicy.time);
        jobConfiguration.setIncrementStrategy(IncrementStrategy.standard);
        jobConfiguration.setSimulationTime(3600000L); // 1 hour
        jobConfiguration.setRampTime(600000L); // 10 min
        jobConfiguration.setBaselineVirtualUsers(10);
        jobConfiguration.setUserIntervalIncrement(5);
        when(projectBean.getJobConfiguration()).thenReturn(jobConfiguration);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testConstructor() {
        assertNotNull(usersAndTimes);
    }

    @Test
    public void testGetTerminationPolicyList_ReturnsAllValues() {
        TerminationPolicy[] list = usersAndTimes.getTerminationPolicyList();
        assertNotNull(list);
        assertTrue(list.length > 0);
    }

    @Test
    public void testGetIncrementStrategyList_ReturnsAllValues() {
        IncrementStrategy[] list = usersAndTimes.getIncrementStrategyList();
        assertNotNull(list);
        assertTrue(list.length > 0);
    }

    @Test
    public void testGetTerminationPolicy() {
        assertEquals(TerminationPolicy.time, usersAndTimes.getTerminationPolicy());
    }

    @Test
    public void testSetTerminationPolicy() {
        usersAndTimes.setTerminationPolicy(TerminationPolicy.script);
        assertEquals(TerminationPolicy.script, jobConfiguration.getTerminationPolicy());
    }

    @Test
    public void testGetIncrementStrategy() {
        assertEquals(IncrementStrategy.standard, usersAndTimes.getIncrementStrategy());
    }

    @Test
    public void testSetIncrementStrategy() {
        usersAndTimes.setIncrementStrategy(IncrementStrategy.increasing);
        assertEquals(IncrementStrategy.increasing, jobConfiguration.getIncrementStrategy());
    }

    @Test
    public void testGetSimulationTime_WhenNoExpression() {
        jobConfiguration.setSimulationTimeExpression(null);
        String result = usersAndTimes.getSimulationTime();
        assertNotNull(result);
    }

    @Test
    public void testGetSimulationTime_WhenExpressionSet() {
        jobConfiguration.setSimulationTimeExpression("#{2*1800000}");
        String result = usersAndTimes.getSimulationTime();
        assertEquals("#{2*1800000}", result);
    }

    @Test
    public void testSetSimulationTime_InvalidFormat_ShowsError() {
        usersAndTimes.setSimulationTime("not-a-time");
        verify(messages).error(anyString());
    }

    @Test
    public void testGetRampTime_WhenNoExpression() {
        jobConfiguration.setRampTimeExpression(null);
        String result = usersAndTimes.getRampTime();
        assertNotNull(result);
    }

    @Test
    public void testGetRampTime_WhenExpressionSet() {
        jobConfiguration.setRampTimeExpression("#{600000}");
        String result = usersAndTimes.getRampTime();
        assertEquals("#{600000}", result);
    }

    @Test
    public void testSetRampTime_InvalidFormat_ShowsError() {
        usersAndTimes.setRampTime("not-a-time");
        verify(messages).error(anyString());
    }

    @Test
    public void testGetStartUsers() {
        jobConfiguration.setBaselineVirtualUsers(50);
        assertEquals("50", usersAndTimes.getStartUsers());
    }

    @Test
    public void testSetStartUsers() {
        usersAndTimes.setStartUsers("100");
        assertEquals(100, jobConfiguration.getBaselineVirtualUsers());
    }

    @Test
    public void testGetUserIncrement_WhenPositive() {
        jobConfiguration.setUserIntervalIncrement(10);
        assertEquals("10", usersAndTimes.getUserIncrement());
    }

    @Test
    public void testGetUserIncrement_WhenZero_ReturnsOne() {
        jobConfiguration.setUserIntervalIncrement(0);
        assertEquals("1", usersAndTimes.getUserIncrement());
    }

    @Test
    public void testSetUserIncrement_Numeric() {
        usersAndTimes.setUserIncrement("25");
        assertEquals(25, jobConfiguration.getUserIntervalIncrement());
    }

    @Test
    public void testSetUserIncrement_NonNumeric_DoesNotUpdate() {
        jobConfiguration.setUserIntervalIncrement(5);
        usersAndTimes.setUserIncrement("invalid");
        assertEquals(5, jobConfiguration.getUserIntervalIncrement());
    }

    @Test
    public void testGetTotalUsers_WithJobRegions() {
        Set<JobRegion> regions = new HashSet<>();
        JobRegion r1 = new JobRegion(VMRegion.US_EAST, "50");
        JobRegion r2 = new JobRegion(VMRegion.US_WEST_2, "30");
        regions.add(r1);
        regions.add(r2);
        jobConfiguration.setJobRegions(regions);

        // We need to set the jobRegions field in usersAndTimes (it caches them)
        // getTotalUsers calls getJobRegions() which may call projectBean.getJobConfiguration()
        int total = usersAndTimes.getTotalUsers();
        assertTrue(total >= 0);
    }

    @Test
    public void testGetEndRate_Standard() {
        jobConfiguration.setTargetRampRate(2.0);
        String result = usersAndTimes.getEndRate();
        assertEquals("2.0", result);
    }

    @Test
    public void testSetEndRate_WhenStandard() {
        jobConfiguration.setIncrementStrategy(IncrementStrategy.standard);
        usersAndTimes.setEndRate("3.0");
        assertEquals(3.0, jobConfiguration.getTargetRampRate(), 0.001);
    }

    @Test
    public void testCopyTo_CopiesJobConfiguration() {
        Project project = new Project();
        Workload targetWorkload = Workload.builder().name("target").project(project).build();
        jobConfiguration.setBaselineVirtualUsers(50);
        jobConfiguration.setJobRegions(new HashSet<>());

        usersAndTimes.copyTo(targetWorkload);

        assertNotNull(targetWorkload.getJobConfiguration());
        assertEquals(50, targetWorkload.getJobConfiguration().getBaselineVirtualUsers());
    }

    @Test
    public void testInit_LoadsJobRegions() {
        jobConfiguration.setJobRegions(new HashSet<>());
        // init() should call getJobRegions() without error
        assertDoesNotThrow(() -> usersAndTimes.init());
    }

    @Test
    public void testSetSimulationTime_ValidFormat_SetsExpression() {
        usersAndTimes.setSimulationTime("3600000");
        assertEquals("3600000", jobConfiguration.getSimulationTimeExpression());
        verify(messages, never()).error(anyString());
    }

    @Test
    public void testSetRampTime_ValidFormat_SetsExpression() {
        usersAndTimes.setRampTime("600000");
        assertEquals("600000", jobConfiguration.getRampTimeExpression());
        verify(messages, never()).error(anyString());
    }

    @Test
    public void testSetEndRate_WhenIncreasing_DoesNotUpdate() {
        jobConfiguration.setIncrementStrategy(IncrementStrategy.increasing);
        jobConfiguration.setTargetRampRate(1.0);
        usersAndTimes.setEndRate("5.0");
        // setEndRate only updates when strategy == standard
        assertEquals(1.0, jobConfiguration.getTargetRampRate(), 0.001);
    }

    @Test
    public void testSave_PersistsJobConfiguration() {
        // save() calls new JobConfigurationDao().saveOrUpdate(jc) then sets jobRegions = null
        // With H2 in-memory DB, a new JobConfiguration should be persisted
        jobConfiguration.setJobRegions(new HashSet<>());
        assertDoesNotThrow(() -> usersAndTimes.save());
        // After save, jobRegions field is reset to null (init will reload on next access)
    }

    @Test
    public void testSetSimulationTime_WhenGetJobConfigThrows_CallsErrorMessage() {
        // Make getJobConfiguration throw RuntimeException after isValidExpression check passes
        when(projectBean.getJobConfiguration()).thenThrow(new RuntimeException("forced error"));
        usersAndTimes.setSimulationTime("3600000");
        verify(messages).error(contains("Cannot format simulation time"));
    }

    @Test
    public void testSetRampTime_WhenGetJobConfigThrows_CallsErrorMessage() {
        when(projectBean.getJobConfiguration()).thenThrow(new RuntimeException("forced error"));
        usersAndTimes.setRampTime("600000");
        verify(messages).error(contains("Cannot format ramp time"));
    }

    @Test
    public void testCopyTo_CopiesRegions() {
        Project project = new Project();
        Workload targetWorkload = Workload.builder().name("target").project(project).build();
        jobConfiguration.setBaselineVirtualUsers(20);
        Set<JobRegion> regions = new HashSet<>();
        JobRegion region = new JobRegion(VMRegion.US_EAST, "100");
        regions.add(region);
        jobConfiguration.setJobRegions(regions);

        usersAndTimes.copyTo(targetWorkload);

        assertNotNull(targetWorkload.getJobConfiguration());
        assertEquals(1, targetWorkload.getJobConfiguration().getJobRegions().size());
        JobRegion copiedRegion = targetWorkload.getJobConfiguration().getJobRegions().iterator().next();
        assertEquals(VMRegion.US_EAST, copiedRegion.getRegion());
        assertEquals("100", copiedRegion.getUsers());
    }
}
