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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobVMInstance;
import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.api.enumerated.VMRole;

/**
 * The class <code>JobVMInstanceTest</code> contains tests for the class <code>{@link JobVMInstance}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class JobVMInstanceTest {
    /**
     * Run the JobVMInstance() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testJobVMInstance_1()
        throws Exception {
        JobVMInstance result = new JobVMInstance();
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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        Object obj = new JobVMInstance();

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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        Object obj = new JobVMInstance();

        boolean result = fixture.equals(obj);
        assertTrue(result);
    }

    /**
     * Run the JobConfiguration getJob() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetJob_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        JobConfiguration result = fixture.getJob();
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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        String result = fixture.getStatus();
        assertNotNull(result);
    }

    /**
     * Run the int getUserCount() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUserCount_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        int result = fixture.getUserCount();
    }

    /**
     * Run the VMInstance getVMInstance() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVMInstance_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        VMInstance result = fixture.getVMInstance();
        assertNotNull(result);
    }

    /**
     * Run the VMRole getVmRole() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetVmRole_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        VMRole result = fixture.getVmRole();
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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        int result = fixture.hashCode();
    }

    /**
     * Run the void setJob(JobConfiguration) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetJob_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        JobConfiguration job = new JobConfiguration();

        fixture.setJob(job);
    }

    /**
     * Run the void setParent(JobConfiguration) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetParent_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        JobConfiguration job = new JobConfiguration();

        fixture.setParent(job);
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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        String status = "";

        fixture.setStatus(status);
    }

    /**
     * Run the void setUserCount(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUserCount_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        int userCount = 1;

        fixture.setUserCount(userCount);
    }

    /**
     * Run the void setVMInstance(VMInstance) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVMInstance_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        VMInstance instance = new VMInstance();

        fixture.setVMInstance(instance);
    }

    /**
     * Run the void setVmRole(VMRole) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetVmRole_1()
        throws Exception {
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);
        VMRole vmRole = VMRole.Agent;

        fixture.setVmRole(vmRole);
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
        JobVMInstance fixture = new JobVMInstance();
        fixture.setUserCount(1);
        fixture.setVMInstance(new VMInstance());
        fixture.setStatus("");
        fixture.setJob(new JobConfiguration());
        fixture.setVmRole(VMRole.Agent);

        String result = fixture.toString();
        assertNotNull(result);
    }
}