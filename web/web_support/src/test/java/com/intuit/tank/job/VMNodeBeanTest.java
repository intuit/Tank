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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.job.JobNodeBean;
import com.intuit.tank.job.VMNodeBean;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>VMNodeBeanTest</code> contains tests for the class <code>{@link VMNodeBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class VMNodeBeanTest {
    /**
     * Run the VMNodeBean(CloudVmStatus,boolean) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testVMNodeBean_1()
            throws Exception {
        CloudVmStatus vmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1,
                VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        vmStatus.setTotalTps(1);
        vmStatus.setUserDetails(new LinkedList());
        boolean hasRights = true;

        VMNodeBean result = new VMNodeBean(vmStatus, hasRights);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.api.enumerated.VMImageType.<init>(VMImageType.java:18)
        // at com.intuit.tank.api.enumerated.VMImageType.<clinit>(VMImageType.java:4)
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertNotNull(result);
    }

    /**
     * Run the List<JobNodeBean> getSubNodes() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetSubNodes_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        List<JobNodeBean> result = fixture.getSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertNotNull(result);
    }

    /**
     * Run the String getType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetType_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        String result = fixture.getType();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertNotNull(result);
    }

    /**
     * Run the boolean hasSubNodes() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testHasSubNodes_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        boolean result = fixture.hasSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertTrue(!result);
    }

    /**
     * Run the boolean isKillable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsKillable_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        boolean result = fixture.isKillable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertTrue(!result);
    }

    /**
     * Run the boolean isPausable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsPausable_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        boolean result = fixture.isPausable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRampPausable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsRampPausable_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        boolean result = fixture.isRampPausable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRunnable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsRunnable_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        boolean result = fixture.isRunnable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertTrue(!result);
    }

    /**
     * Run the boolean isStopable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsStopable_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        boolean result = fixture.isStopable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
        assertTrue(!result);
    }

    /**
     * Run the void reCalculate() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testReCalculate_1()
            throws Exception {
        CloudVmStatus cloudVmStatus = new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT,
                VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        cloudVmStatus.setTotalTps(1);
        cloudVmStatus.setUserDetails(new LinkedList());
        VMNodeBean fixture = new VMNodeBean(cloudVmStatus, true);

        fixture.reCalculate();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.VMImageType
        // at sun.misc.Unsafe.ensureClassInitialized(Native Method)
    }
}