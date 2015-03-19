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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Workload;

/**
 * The class <code>JobConfigurationTest</code> contains tests for the class <code>{@link JobConfiguration}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobConfigurationTest {
    /**
     * Run the JobConfiguration() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobConfiguration_1()
        throws Exception {

        JobConfiguration result = new JobConfiguration();
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Object obj = new Object();

        boolean result = fixture.equals(obj);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Object obj = new JobConfiguration();

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Object obj = new JobConfiguration();

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the Set<Integer> getDataFileIds() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetDataFileIds_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        Set<Integer> result = fixture.getDataFileIds();
        assertNotNull(result);
    }

    /**
     * Run the Set<JobRegion> getJobRegions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobRegions_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        Set<JobRegion> result = fixture.getJobRegions();
        assertNotNull(result);
    }

    /**
     * Run the Set<JobNotification> getNotifications() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetNotifications_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        Set<JobNotification> result = fixture.getNotifications();
        assertNotNull(result);
    }

    /**
     * Run the int getTotalVirtualUsers() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetTotalVirtualUsers_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        int result = fixture.getTotalVirtualUsers();
        assertEquals(0, result);
    }

    /**
     * Run the Map<String, String> getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVariables_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(null);
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        Map<String, String> result = fixture.getVariables();
        assertNotNull(result);
    }

    /**
     * Run the Map<String, String> getVariables() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVariables_2()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        Map<String, String> result = fixture.getVariables();
        assertNotNull(result);
    }

    /**
     * Run the Workload getWorkload() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetWorkload_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        Workload result = fixture.getWorkload();
        assertNotNull(result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        int result = fixture.hashCode();
    }

    /**
     * Run the void setDataFileIds(Set<Integer>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetDataFileIds_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Set<Integer> dataFileIds = new HashSet();

        fixture.setDataFileIds(dataFileIds);
    }

    /**
     * Run the void setJobRegions(Set<JobRegion>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobRegions_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Set<JobRegion> jobRegions = new HashSet();

        fixture.setJobRegions(jobRegions);
    }

    /**
     * Run the void setNotifications(Set<JobNotification>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetNotifications_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Set<JobNotification> notifications = new HashSet();

        fixture.setNotifications(notifications);
    }

    /**
     * Run the void setParent(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetParent_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Workload workload = new Workload();

        fixture.setParent(workload);
    }

    /**
     * Run the void setVariables(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVariables_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Map<String, String> variables = new HashMap();

        fixture.setVariables(variables);
    }

    /**
     * Run the void setWorkload(Workload) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetWorkload_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());
        Workload workload = new Workload();

        fixture.setWorkload(workload);
    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        JobConfiguration fixture = new JobConfiguration();
        fixture.setDataFileIds(new HashSet());
        fixture.setVariables(new HashMap());
        fixture.setJobRegions(new HashSet());
        fixture.setNotifications(new HashSet());
        fixture.setParent(new Workload());

        String result = fixture.toString();
        assertNotNull(result);
    }
}