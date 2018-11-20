package com.intuit.tank.project;

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

import com.intuit.tank.project.CreateProjectBean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>CreateProjectBeanTest</code> contains tests for the class <code>{@link CreateProjectBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class CreateProjectBeanTest {
    /**
     * Run the CreateProjectBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCreateProjectBean_1()
        throws Exception {
        CreateProjectBean result = new CreateProjectBean();
        assertNotNull(result);
    }


    /**
     * Run the void cancel() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCancel_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");

        fixture.cancel();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }


    /**
     * Run the String getComments() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetComments_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");

        String result = fixture.getComments();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");

        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");

        String result = fixture.getProductName();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the String getScriptDriver() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetScriptDriver_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");

        String result = fixture.getScriptDriver();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
        assertNotNull(result);
    }

    /**
     * Run the void setComments(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetComments_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");
        String comments = "";

        fixture.setComments(comments);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");
        String productName = "";

        fixture.setProductName(productName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }

    /**
     * Run the void setScriptDriver(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetScriptDriver_1()
        throws Exception {
        CreateProjectBean fixture = new CreateProjectBean();
        fixture.setComments("");
        fixture.setProductName("");
        fixture.setName("");
        fixture.setScriptDriver("");
        String scriptDriver = "";

        fixture.setScriptDriver(scriptDriver);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }
}