package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.intuit.tank.project.BaseJob;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;

/**
 * The class <code>BaseJobTest</code> contains tests for the class <code>{@link BaseJob}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class BaseJobTest {
    /**
     * Run the int getBaselineVirtualUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetBaselineVirtualUsers_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        int result = fixture.getBaselineVirtualUsers();

        assertEquals(0, result);
    }

    /**
     * Run the Long getExecutionTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetExecutionTime_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

    }

    /**
     * Run the IncrementStrategy getIncrementStrategy() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetIncrementStrategy_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        IncrementStrategy result = fixture.getIncrementStrategy();

        assertNotNull(result);
    }

    /**
     * Run the String getLocation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetLocation_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getLocation();
        assertNotNull(result);
    }

    /**
     * Run the String getLoggingProfile() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetLoggingProfile_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getLoggingProfile();
        assertNotNull(result);
    }

    /**
     * Run the int getNumUsersPerAgent() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetNumUsersPerAgent_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        int result = fixture.getNumUsersPerAgent();
    }

    /**
     * Run the long getRampTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRampTime_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        long result = fixture.getRampTime();
        assertEquals(0L, result);
    }

    /**
     * Run the long getRampTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRampTime_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        long result = fixture.getRampTime();
        assertEquals(0L, result);
    }

    /**
     * Run the String getRampTimeExpression() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRampTimeExpression_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getRampTimeExpression();
    }

    /**
     * Run the String getReportingMode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetReportingMode_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getReportingMode();
        assertNotNull(result);
    }

    /**
     * Run the long getSimulationTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSimulationTime_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        long result = fixture.getSimulationTime();
        assertEquals(0L, result);
    }

    /**
     * Run the long getSimulationTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSimulationTime_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        long result = fixture.getSimulationTime();
        assertEquals(0L, result);
    }

    /**
     * Run the String getSimulationTimeExpression() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSimulationTimeExpression_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getSimulationTimeExpression();
    }

    /**
     * Run the String getStopBehavior() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStopBehavior_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getStopBehavior();
        assertNotNull(result);
    }

    /**
     * Run the String getStopBehavior() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStopBehavior_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getStopBehavior();
        assertNotNull(result);
    }

    /**
     * Run the TerminationPolicy getTerminationPolicy() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTerminationPolicy_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        TerminationPolicy result = fixture.getTerminationPolicy();
        assertNotNull(result);
    }

    /**
     * Run the int getUserIntervalIncrement() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUserIntervalIncrement_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        int result = fixture.getUserIntervalIncrement();
        assertEquals(0, result);
    }

    /**
     * Run the String getVmInstanceType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVmInstanceType_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        String result = fixture.getVmInstanceType();

        assertNotNull(result);
    }

    /**
     * Run the boolean isAllowOverride() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsAllowOverride_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        boolean result = fixture.isAllowOverride();

    }

    /**
     * Run the boolean isAllowOverride() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsAllowOverride_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        boolean result = fixture.isAllowOverride();
    }

    /**
     * Run the boolean isAllowOverride() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testIsAllowOverride_3()
        throws Exception {
        BaseJob fixture = new JobConfiguration();

        boolean result = fixture.isAllowOverride();
    }

    /**
     * Run the void setAllowOverride(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetAllowOverride_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        boolean allowOverride = true;

        fixture.setAllowOverride(allowOverride);
    }

    /**
     * Run the void setBaselineVirtualUsers(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetBaselineVirtualUsers_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        int baselineVirtualUsers = 1;

        fixture.setBaselineVirtualUsers(baselineVirtualUsers);
    }

    /**
     * Run the void setExecutionTime(Long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetExecutionTime_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        Long executionTime = new Long(1L);

        fixture.setExecutionTime(executionTime);
    }

    /**
     * Run the void setIncrementStrategy(IncrementStrategy) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetIncrementStrategy_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        IncrementStrategy incrementStrategy = IncrementStrategy.increasing;

        fixture.setIncrementStrategy(incrementStrategy);
    }

    /**
     * Run the void setIncrementStrategy(IncrementStrategy) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetIncrementStrategy_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        IncrementStrategy incrementStrategy = null;

        fixture.setIncrementStrategy(incrementStrategy);
    }

    /**
     * Run the void setLocation(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLocation_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String location = "";

        fixture.setLocation(location);
    }

    /**
     * Run the void setLocation(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLocation_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String location = null;

        fixture.setLocation(location);
    }

    /**
     * Run the void setLoggingProfile(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetLoggingProfile_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String loggingProfile = "";

        fixture.setLoggingProfile(loggingProfile);
    }

    /**
     * Run the void setNumUsersPerAgent(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetNumUsersPerAgent_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        int numUsersPerAgent = 1;

        fixture.setNumUsersPerAgent(numUsersPerAgent);
    }

    /**
     * Run the void setRampTime(long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRampTime_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        long rampTime = 1L;

        fixture.setRampTime(rampTime);
    }

    /**
     * Run the void setRampTimeExpression(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRampTimeExpression_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String rampTimeExpression = "";

        fixture.setRampTimeExpression(rampTimeExpression);
    }

    /**
     * Run the void setReportingMode(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetReportingMode_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String reportingMode = "";

        fixture.setReportingMode(reportingMode);
    }

    /**
     * Run the void setReportingMode(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetReportingMode_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String reportingMode = null;

        fixture.setReportingMode(reportingMode);
    }

    /**
     * Run the void setSimulationTime(long) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSimulationTime_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        long simulationTime = 1L;

        fixture.setSimulationTime(simulationTime);
    }

    /**
     * Run the void setSimulationTimeExpression(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSimulationTimeExpression_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String simulationTimeExpression = "";

        fixture.setSimulationTimeExpression(simulationTimeExpression);
    }

    /**
     * Run the void setStopBehavior(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetStopBehavior_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String stopBehavior = "";

        fixture.setStopBehavior(stopBehavior);
    }

    /**
     * Run the void setTerminationPolicy(TerminationPolicy) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTerminationPolicy_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        TerminationPolicy terminationPolicy = TerminationPolicy.script;

        fixture.setTerminationPolicy(terminationPolicy);
    }

    /**
     * Run the void setTerminationPolicy(TerminationPolicy) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetTerminationPolicy_2()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        TerminationPolicy terminationPolicy = null;

        fixture.setTerminationPolicy(terminationPolicy);
    }

    /**
     * Run the void setUserIntervalIncrement(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUserIntervalIncrement_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        int userIntervalIncrement = 1;

        fixture.setUserIntervalIncrement(userIntervalIncrement);
    }

    /**
     * Run the void setVmInstanceType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVmInstanceType_1()
        throws Exception {
        BaseJob fixture = new JobConfiguration();
        String vmInstanceType = "";

        fixture.setVmInstanceType(vmInstanceType);
    }
}