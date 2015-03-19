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

import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.job.ActJobNodeBean;
import com.intuit.tank.job.ProjectNodeBean;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;

/**
 * The class <code>ProjectNodeBeanTest</code> contains tests for the class <code>{@link ProjectNodeBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ProjectNodeBeanTest {
    /**
     * Run the ProjectNodeBean(Project) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testProjectNodeBean_1()
        throws Exception {
        Project prj = new Project();
        prj.setName("");

        ProjectNodeBean result = new ProjectNodeBean(prj);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the ProjectNodeBean(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testProjectNodeBean_2()
        throws Exception {
        String name = "";

        ProjectNodeBean result = new ProjectNodeBean(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.job.ProjectNodeBean.<init>(ProjectNodeBean.java:32)
        assertNotNull(result);
    }

    /**
     * Run the void addJob(ActJobNodeBean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testAddJob_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());
        ActJobNodeBean jobNode = new ActJobNodeBean(new JobInstance(), true);

        fixture.addJob(jobNode);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the List<ActJobNodeBean> getJobBeans() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetJobBeans_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        List<ActJobNodeBean> result = fixture.getJobBeans();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the List<ActJobNodeBean> getSubNodes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetSubNodes_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        List<ActJobNodeBean> result = fixture.getSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        String result = fixture.getType();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the boolean hasSubNodes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testHasSubNodes_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.hasSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean hasSubNodes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testHasSubNodes_2()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.hasSubNodes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isKillable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsKillable_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.isKillable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isPausable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsPausable_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.isPausable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRampPausable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsRampPausable_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.isRampPausable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isRunnable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsRunnable_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.isRunnable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the boolean isStopable() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsStopable_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        boolean result = fixture.isStopable();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(!result);
    }

    /**
     * Run the void reCalculate() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReCalculate_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        fixture.reCalculate();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void reCalculate() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testReCalculate_2()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());

        fixture.reCalculate();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setJobBeans(List<ActJobNodeBean>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetJobBeans_1()
        throws Exception {
        Project project = new Project();
        project.setName("");
        ProjectNodeBean fixture = new ProjectNodeBean(project);
        fixture.setJobBeans(new LinkedList());
        List<ActJobNodeBean> jobBeans = new LinkedList();

        fixture.setJobBeans(jobBeans);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
    }
}