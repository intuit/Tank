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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Test;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * The class <code>ActJobNodeBeanTest</code> contains tests for the class <code>{@link ActJobNodeBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ActJobNodeBeanTest {
    /**
     * Run the ActJobNodeBean(JobInstance,boolean) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testActJobNodeBean_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance job = new JobInstance(workload, "");
        job.setEndTime(new Date());
        job.setJobDetails("");
        job.setTotalVirtualUsers(1);
        job.setStatus(JobQueueStatus.Aborted);
        job.setStartTime(new Date());
        boolean hasRights = true;

        ActJobNodeBean result = new ActJobNodeBean(job, hasRights, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<init>(IncrementStrategy.java:23)
        //       at com.intuit.tank.api.enumerated.IncrementStrategy.<clinit>(IncrementStrategy.java:13)
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertNotNull(result);
    }

    /**
     * Run the ActJobNodeBean(String,CloudVmStatusContainer) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testActJobNodeBean_2()
        throws Exception {
        String jobId = "";
        CloudVmStatusContainer container = new CloudVmStatusContainer();
        container.setStatus(JobQueueStatus.Aborted);
        container.setEndTime(new Date());
        container.setStartTime(new Date());

        ActJobNodeBean result = new ActJobNodeBean(jobId, container, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer.<init>(CloudVmStatusContainer.java:79)
        assertNotNull(result);
    }

    /**
     * Run the void addVMBean(VMNodeBean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testAddVMBean_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());
        VMNodeBean vmNode = new VMNodeBean(new CloudVmStatus("", "", "", JobStatus.Completed, VMImageType.AGENT, VMRegion.ASIA_1, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date()), true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));

        fixture.addVMBean(vmNode);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
    }

    /**
     * Run the String getJobDetails() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetJobDetails_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        String result = fixture.getJobDetails();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertNotNull(result);
    }

    /**
     * Run the List<VMNodeBean> getSubNodes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSubNodes_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        List<VMNodeBean> result = fixture.getSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertNotNull(result);
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        String result = fixture.getType();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertNotNull(result);
    }

    /**
     * Run the List<VMNodeBean> getVmBeans() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetVmBeans_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        List<VMNodeBean> result = fixture.getVmBeans();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertNotNull(result);
    }

    /**
     * Run the boolean hasSubNodes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasSubNodes_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.hasSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean hasSubNodes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasSubNodes_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.hasSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isDeleteable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsDeleteable_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isDeleteable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(result);
    }

    /**
     * Run the boolean isJobNode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsJobNode_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isJobNode();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(result);
    }

    /**
     * Run the boolean isKillable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsKillable_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isKillable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isKillable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsKillable_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isKillable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isPausable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsPausable_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isPausable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isPausable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsPausable_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isPausable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRampPausable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsRampPausable_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isRampPausable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRampPausable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsRampPausable_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isRampPausable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRunnable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsRunnable_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isRunnable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRunnable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsRunnable_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isRunnable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isStopable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsStopable_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isStopable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the boolean isStopable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsStopable_2()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        boolean result = fixture.isStopable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
        assertTrue(!result);
    }

    /**
     * Run the void reCalculate() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testReCalculate_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());

        fixture.reCalculate();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
    }

    /**
     * Run the void setVmBeans(List<VMNodeBean>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetVmBeans_1()
        throws Exception {
        Workload workload = new Workload();
        workload.setJobConfiguration(new JobConfiguration());
        JobInstance jobInstance = new JobInstance(workload, "");
        jobInstance.setEndTime(new Date());
        jobInstance.setJobDetails("");
        jobInstance.setTotalVirtualUsers(1);
        jobInstance.setStatus(JobQueueStatus.Aborted);
        jobInstance.setStartTime(new Date());
        ActJobNodeBean fixture = new ActJobNodeBean(jobInstance, true, FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM));
        fixture.setVmBeans(new LinkedList());
        List<VMNodeBean> vmBeans = new LinkedList();

        fixture.setVmBeans(vmBeans);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.api.enumerated.IncrementStrategy
        //       at com.intuit.tank.project.BaseJob.<init>(BaseJob.java:28)
        //       at com.intuit.tank.project.JobConfiguration.<init>(JobConfiguration.java:63)
        //       at com.intuit.tank.project.Workload.<init>(Workload.java:57)
    }
}