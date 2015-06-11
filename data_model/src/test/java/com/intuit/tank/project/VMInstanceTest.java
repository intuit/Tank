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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.intuit.tank.project.JobVMInstance;
import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>VMInstanceTest</code> contains tests for the class <code>{@link VMInstance}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class VMInstanceTest {
    /**
     * Run the VMInstance() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testVMInstance_1()
        throws Exception {
        VMInstance result = new VMInstance();
        assertNotNull(result);
    }

    /**
     * Run the VMInstance.Builder builder() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilder_1()
        throws Exception {

        VMInstance.Builder result = VMInstance.builder();

        assertNotNull(result);
    }

    /**
     * Run the VMInstance.Builder builderFrom(VMInstance) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testBuilderFrom_1()
        throws Exception {
        VMInstance image = new VMInstance();

        VMInstance.Builder result = VMInstance.builderFrom(image);

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
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
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
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        Object obj = new VMInstance();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        Object obj = new VMInstance();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getAmiId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetAmiId_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        String result = fixture.getAmiId();

        assertEquals("", result);
    }

    /**
     * Run the Date getEndTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetEndTime_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        Date result = fixture.getEndTime();

        assertNotNull(result);
    }

    /**
     * Run the String getInstanceId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetInstanceId_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        String result = fixture.getInstanceId();

        assertEquals("", result);
    }

    /**
     * Run the String getJobId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJobId_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the VMRegion getRegion() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetRegion_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        VMRegion result = fixture.getRegion();

        assertNotNull(result);
        assertEquals("Asia Pacific (Singapore)", result.toString());
        assertEquals("ap-southeast-1", result.getRegion());
        assertEquals("ec2.ap-southeast-1.amazonaws.com", result.getEndpoint());
        assertEquals("Asia Pacific (Singapore)", result.getDescription());
        assertEquals("ASIA_1", result.name());
    }

    /**
     * Run the String getSize() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetSize_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        String result = fixture.getSize();

        assertEquals("", result);
    }

    /**
     * Run the Date getStartTime() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStartTime_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        Date result = fixture.getStartTime();

        assertNotNull(result);
    }

    /**
     * Run the String getStatus() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetStatus_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        String result = fixture.getStatus();

        assertEquals("", result);
    }

    /**
     * Run the Set<JobVMInstance> getVMInstances() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVMInstances_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        Set<JobVMInstance> result = fixture.getVMInstances();

        assertNotNull(result);
        assertEquals(0, result.size());
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
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        int result = fixture.hashCode();

        assertEquals(2145, result);
    }

    /**
     * Run the void setAmiId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetAmiId_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        String amiId = "";

        fixture.setAmiId(amiId);

    }

    /**
     * Run the void setEndTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetEndTime_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        Date endTime = new Date();

        fixture.setEndTime(endTime);

    }

    /**
     * Run the void setInstanceId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetInstanceId_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        String instanceId = "";

        fixture.setInstanceId(instanceId);

    }

    /**
     * Run the void setJobId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJobId_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        String jobId = "";

        fixture.setJobId(jobId);

    }

    /**
     * Run the void setRegion(VMRegion) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetRegion_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        VMRegion region = VMRegion.ASIA_1;

        fixture.setRegion(region);

    }

    /**
     * Run the void setSize(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetSize_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        String size = "";

        fixture.setSize(size);

    }

    /**
     * Run the void setStartTime(Date) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetStartTime_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        Date startTime = new Date();

        fixture.setStartTime(startTime);

    }

    /**
     * Run the void setStatus(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetStatus_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        String status = "";

        fixture.setStatus(status);

    }

    /**
     * Run the void setVMInstances(Set<JobVMInstance>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVMInstances_1()
        throws Exception {
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");
        Set<JobVMInstance> vmImages = new HashSet();

        fixture.setVMInstances(vmImages);

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
        VMInstance fixture = new VMInstance();
        fixture.setRegion(VMRegion.ASIA_1);
        fixture.setInstanceId("");
        fixture.setJobId("");
        fixture.setSize("");
        fixture.setVMInstances(new HashSet());
        fixture.setStatus("");
        fixture.setEndTime(new Date());
        fixture.setStartTime(new Date());
        fixture.setAmiId("");

        String result = fixture.toString();

    }
}