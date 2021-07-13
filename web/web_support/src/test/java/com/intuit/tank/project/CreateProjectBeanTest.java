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

import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>CreateProjectBeanTest</code> contains tests for the class <code>{@link CreateProjectBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
@EnableAutoWeld
@ActivateScopes(RequestScoped.class)
public class CreateProjectBeanTest {
    
    @Inject
    private CreateProjectBean createProjectBean;
    
    /**
     * Run the CreateProjectBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testCreateProjectBean_1()
        throws Exception {
        assertNotNull(createProjectBean);
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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        createProjectBean.cancel();

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getComments();

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getName();

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getProductName();

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");

        String result = createProjectBean.getScriptDriver();

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String comments = "";

        createProjectBean.setComments(comments);

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String name = "";

        createProjectBean.setName(name);

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String productName = "";

        createProjectBean.setProductName(productName);

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
        createProjectBean.setComments("");
        createProjectBean.setProductName("");
        createProjectBean.setName("");
        createProjectBean.setScriptDriver("");
        String scriptDriver = "";

        createProjectBean.setScriptDriver(scriptDriver);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.CreateProjectBean.setComments(CreateProjectBean.java:115)
    }
}