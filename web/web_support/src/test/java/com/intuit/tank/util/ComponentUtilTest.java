package com.intuit.tank.util;

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

import org.junit.*;

import com.intuit.tank.util.ComponentUtil;

import static org.junit.Assert.*;

/**
 * The class <code>ComponentUtilTest</code> contains tests for the class <code>{@link ComponentUtil}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:51 PM
 */
public class ComponentUtilTest {
    /**
     * Run the String extractId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testExtractId_1()
        throws Exception {
        String clientId = "";

        String result = ComponentUtil.extractId(clientId);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ComponentUtil.extractId(ComponentUtil.java:27)
        assertNotNull(result);
    }

    /**
     * Run the String extractId(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testExtractId_2()
        throws Exception {
        String clientId = "";

        String result = ComponentUtil.extractId(clientId);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ComponentUtil.extractId(ComponentUtil.java:27)
        assertNotNull(result);
    }
}