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

import com.intuit.tank.PropertyComparer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>PropertyComparerTest</code> contains tests for the class <code>{@link PropertyComparer}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class PropertyComparerTest {
    /**
     * Run the PropertyComparer(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testPropertyComparer_1()
        throws Exception {
        String propertyName = "";

        PropertyComparer result = new PropertyComparer(propertyName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PropertyComparer.<init>(PropertyComparer.java:35)
        //       at com.intuit.tank.PropertyComparer.<init>(PropertyComparer.java:26)
        assertNotNull(result);
    }

    /**
     * Run the PropertyComparer(String,SortOrder) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testPropertyComparer_2()
        throws Exception {
        String propertyName = "";
        PropertyComparer.SortOrder sortOrder = PropertyComparer.SortOrder.ASCENDING;

        PropertyComparer result = new PropertyComparer(propertyName, sortOrder);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.PropertyComparer.<init>(PropertyComparer.java:35)
        assertNotNull(result);
    }
}