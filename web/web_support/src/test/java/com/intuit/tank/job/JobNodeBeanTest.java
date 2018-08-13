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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.auth.Security;
import com.intuit.tank.job.JobNodeBean;
import com.intuit.tank.job.ProjectNodeBean;
import com.intuit.tank.project.Project;

/**
 * The class <code>JobNodeBeanTest</code> contains tests for the class <code>{@link JobNodeBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class JobNodeBeanTest {
    
    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        Object obj = null;

        boolean result = fixture.equals(obj);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        Object obj = new ProjectNodeBean(new Project());

        boolean result = fixture.equals(obj);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testEquals_4()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        Object obj = new ProjectNodeBean(new Project());

        boolean result = fixture.equals(obj);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(result);
    }

    /**
     * Run the String getActiveUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetActiveUsers_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getActiveUsers();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getEndTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetEndTime_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getEndTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetId_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getJobId();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        fixture.setName("name:");
        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the ValidationStatus getNumFailures() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetNumFailures_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        ValidationStatus result = fixture.getNumFailures();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getRegion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRegion_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getRegion();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getReportMode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetReportMode_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getReportMode();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getStartTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetStartTime_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getStartTime();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getStatus() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetStatus_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getStatus();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the Map<Date, List<UserDetail>> getStatusDetailMap() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetStatusDetailMap_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        Map<Date, List<UserDetail>> result = fixture.getStatusDetailMap();
    }

    /**
     * Run the String getTotalFails() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTotalFails_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getTotalFails();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getTotalUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTotalUsers_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        String result = fixture.getTotalUsers();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the int getTps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTps_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        int result = fixture.getTps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertEquals(0, result);
    }

    /**
     * Run the int getTps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTps_2()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        int result = fixture.getTps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertEquals(0, result);
    }

    /**
     * Run the int getTps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTps_3()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        int result = fixture.getTps();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertEquals(0, result);
    }

    /**
     * Run the List<UserDetail> getUserDetails() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetUserDetails_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        List<UserDetail> result = fixture.getUserDetails();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        int result = fixture.hashCode();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
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
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        boolean result = fixture.isDeleteable();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isHasRights() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsHasRights_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        boolean result = fixture.isHasRights();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isHasRights() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsHasRights_2()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        boolean result = fixture.isHasRights();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
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
        JobNodeBean fixture = new ProjectNodeBean(new Project());

        boolean result = fixture.isJobNode();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the void setActiveUsers(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetActiveUsers_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String activeUsers = "";

        fixture.setActiveUsers(activeUsers);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setEndTime(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetEndTime_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String endTime = "";

        fixture.setEndTime(endTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setHasRights(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetHasRights_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        boolean hasRights = true;

        fixture.setHasRights(hasRights);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetId_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String id = "";

        fixture.setId(id);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setJobId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetJobId_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String jobId = "";

        fixture.setJobId(jobId);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setNumFailures(ValidationStatus) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetNumFailures_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        ValidationStatus fails = new ValidationStatus();

        fixture.setNumFailures(fails);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setRegion(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetRegion_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String region = "";

        fixture.setRegion(region);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setReportMode(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetReportMode_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String reportMode = "";

        fixture.setReportMode(reportMode);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setStartTime(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetStartTime_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String startTime = "";

        fixture.setStartTime(startTime);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setStatus(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetStatus_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String status = "";

        fixture.setStatus(status);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setStatusDetailMap(Map<Date,List<UserDetail>>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetStatusDetailMap_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        Map<Date, List<UserDetail>> statusDetailMap = new HashMap();

        fixture.setStatusDetailMap(statusDetailMap);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setTotalUsers(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetTotalUsers_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        String totalUsers = "";

        fixture.setTotalUsers(totalUsers);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setTps(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetTps_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        int tps = 1;

        fixture.setTps(tps);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setUserDetails(List<UserDetail>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetUserDetails_1()
            throws Exception {
        JobNodeBean fixture = new ProjectNodeBean(new Project());
        List<UserDetail> userDetails = new LinkedList();

        fixture.setUserDetails(userDetails);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.project.Project.<init>(Project.java:77)
    }
}