package com.intuit.tank.runner;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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

import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.runner.ErrorContainer;

/**
 * The class <code>ErrorContainerTest</code> contains tests for the class <code>{@link ErrorContainer}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class ErrorContainerTest {
    /**
     * Run the ErrorContainer(String,ValidationData,ValidationData,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testErrorContainer_1()
        throws Exception {
        String location = "";
        ValidationData original = new ValidationData();
        ValidationData validation = new ValidationData();
        String reason = "";

        ErrorContainer result = new ErrorContainer(location, original, validation, reason);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.ErrorContainer.<init>(ErrorContainer.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getLocation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetLocation_1()
        throws Exception {
        ErrorContainer fixture = new ErrorContainer("", new ValidationData(), new ValidationData(), "");

        String result = fixture.getLocation();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.ErrorContainer.<init>(ErrorContainer.java:16)
        assertNotNull(result);
    }

    /**
     * Run the ValidationData getOriginalValidation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetOriginalValidation_1()
        throws Exception {
        ErrorContainer fixture = new ErrorContainer("", new ValidationData(), new ValidationData(), "");

        ValidationData result = fixture.getOriginalValidation();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.ErrorContainer.<init>(ErrorContainer.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getReason() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetReason_1()
        throws Exception {
        ErrorContainer fixture = new ErrorContainer("", new ValidationData(), new ValidationData(), "");

        String result = fixture.getReason();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.ErrorContainer.<init>(ErrorContainer.java:16)
        assertNotNull(result);
    }

    /**
     * Run the ValidationData getValidation() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetValidation_1()
        throws Exception {
        ErrorContainer fixture = new ErrorContainer("", new ValidationData(), new ValidationData(), "");

        ValidationData result = fixture.getValidation();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.runner.ErrorContainer.<init>(ErrorContainer.java:16)
        assertNotNull(result);
    }
}