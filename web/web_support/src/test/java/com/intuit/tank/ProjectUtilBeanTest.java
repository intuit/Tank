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

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ProjectUtilBeanTest</code> contains tests for the class <code>{@link ProjectUtilBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
@EnableAutoWeld
@ActivateScopes(SessionScoped.class)
public class ProjectUtilBeanTest {

    @Inject
    private ProjectUtilBean projectUtilBean;
    /**
     * Run the ProjectUtilBean() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testProjectUtilBean_1()
        throws Exception {

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(projectUtilBean);
    }

    /**
     * Run the SelectItem[] getLocations() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetLocations_1()
        throws Exception {

        SelectItem[] result = projectUtilBean.getLocations();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(result);
    }

    /**
     * Run the SelectItem[] getLoggingProfiles() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetLoggingProfiles_1()
        throws Exception {

        SelectItem[] result = projectUtilBean.getLoggingProfiles();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(result);
    }

    /**
     * Run the SelectItem[] getProductNames() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetProductNames_1()
        throws Exception {

        SelectItem[] result = projectUtilBean.getProductNames();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(result);
    }

    /**
     * Run the SelectItem[] getReportingModes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetReportingModes_1()
        throws Exception {

        SelectItem[] result = projectUtilBean.getReportingModes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(result);
    }

    /**
     * Run the SelectItem[] getStopBehaviors() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetStopBehaviors_1()
        throws Exception {

        SelectItem[] result = projectUtilBean.getStopBehaviors();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(result);
    }

    /**
     * Run the SelectItem[] getVmInstanceTypes() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetVmInstanceTypes_1()
        throws Exception {

        SelectItem[] result = projectUtilBean.getVmInstanceTypes();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.ProjectUtilBean.<init>(ProjectUtilBean.java:86)
        assertNotNull(result);
    }
}