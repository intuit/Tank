package com.intuit.tank.script;

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

import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;

import com.intuit.tank.script.TestUploadBean;

/**
 * The class <code>TestUploadBeanTest</code> contains tests for the class <code>{@link TestUploadBean}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class TestUploadBeanTest {
    /**
     * Run the TestUploadBean() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testTestUploadBean_1()
        throws Exception {
        TestUploadBean result = new TestUploadBean();
        assertNotNull(result);
    }

    /**
     * Run the UploadedFile getFile() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetFile_1()
        throws Exception {
        TestUploadBean fixture = new TestUploadBean();
        fixture.setFile(new DefaultUploadedFile());

        UploadedFile result = fixture.getFile();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.TestUploadBean.setFile(TestUploadBean.java:42)
        assertNotNull(result);
    }


    /**
     * Run the void setFile(UploadedFile) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetFile_1()
        throws Exception {
        TestUploadBean fixture = new TestUploadBean();
        fixture.setFile(new DefaultUploadedFile());
        UploadedFile file = new DefaultUploadedFile();

        fixture.setFile(file);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.TestUploadBean.setFile(TestUploadBean.java:42)
    }
}