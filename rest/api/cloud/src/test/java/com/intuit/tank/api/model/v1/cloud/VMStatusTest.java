package com.intuit.tank.api.model.v1.cloud;

/*
 * #%L
 * Cloud Rest API
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.api.model.v1.cloud.VMStatus;

import static org.junit.Assert.*;

/**
 * The class <code>VMStatusTest</code> contains tests for the class <code>{@link VMStatus}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:57 PM
 */
public class VMStatusTest {
    /**
     * Run the VMStatus fromString(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testFromString_1()
        throws Exception {
        String value = "shutting-down";

        VMStatus result = VMStatus.fromString(value);

        assertNotNull(result);
        assertEquals("shutting_down", result.name());
        assertEquals("shutting_down", result.toString());
        assertEquals(6, result.ordinal());
    }

    /**
     * Run the VMStatus fromString(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:57 PM
     */
    @Test
    public void testFromString_2()
        throws Exception {
        String value = VMStatus.rebooting.name();

        VMStatus result = VMStatus.fromString(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.IllegalArgumentException: No enum constant com.intuit.tank.api.model.v1.cloud.VMStatus.
        //       at java.lang.Enum.valueOf(Enum.java:238)
        //       at com.intuit.tank.api.model.v1.cloud.VMStatus.valueOf(VMStatus.java:5)
        //       at com.intuit.tank.api.model.v1.cloud.VMStatus.fromString(VMStatus.java:20)
        assertNotNull(result);
    }
}