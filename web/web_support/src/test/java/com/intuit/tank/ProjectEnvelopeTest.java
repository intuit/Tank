package com.intuit.tank;

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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.ProjectEnvelope;
import com.intuit.tank.project.Project;

/**
 * The class <code>ProjectEnvelopeTest</code> contains tests for the class <code>{@link ProjectEnvelope}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ProjectEnvelopeTest {
    /**
     * Run the ProjectEnvelope() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testProjectEnvelope_1()
        throws Exception {

        ProjectEnvelope result = new ProjectEnvelope();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectEnvelope.<init>(ProjectEnvelope.java:15)
        //       at com.intuit.tank.ProjectEnvelope.<init>(ProjectEnvelope.java:18)
        //       at com.intuit.tank.ProjectEnvelope.<init>(ProjectEnvelope.java:22)
        assertNotNull(result);
    }

    /**
     * Run the ProjectEnvelope(Project) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testProjectEnvelope_2()
        throws Exception {
        Project project = new Project();

        ProjectEnvelope result = new ProjectEnvelope(project);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }

    /**
     * Run the ProjectEnvelope(boolean,Project) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testProjectEnvelope_3()
        throws Exception {
        boolean checked = true;
        Project project = new Project();

        ProjectEnvelope result = new ProjectEnvelope(checked, project);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertNotNull(result);
    }



    /**
     * Run the Project getProject() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetProject_1()
        throws Exception {
        ProjectEnvelope fixture = new ProjectEnvelope(true, new Project());

        Project result = fixture.getProject();
        assertNotNull(result);
    }

    /**
     * Run the boolean isChecked() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsChecked_1()
        throws Exception {
        ProjectEnvelope fixture = new ProjectEnvelope(true, new Project());

        boolean result = fixture.isChecked();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
        assertTrue(result);
    }

    /**
     * Run the boolean isChecked() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsChecked_2()
        throws Exception {
        ProjectEnvelope fixture = new ProjectEnvelope(false, new Project());

        boolean result = fixture.isChecked();
        assertTrue(!result);
    }

    /**
     * Run the void setChecked(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetChecked_1()
        throws Exception {
        ProjectEnvelope fixture = new ProjectEnvelope(true, new Project());
        boolean checked = true;

        fixture.setChecked(checked);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
    }

    /**
     * Run the void setProject(Project) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetProject_1()
        throws Exception {
        ProjectEnvelope fixture = new ProjectEnvelope(true, new Project());
        Project project = new Project();

        fixture.setProject(project);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.Project.<init>(Project.java:77)
    }
}