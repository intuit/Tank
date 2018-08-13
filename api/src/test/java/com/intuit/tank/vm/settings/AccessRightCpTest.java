package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
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

import com.intuit.tank.vm.settings.AccessRight;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>AccessRightCpTest</code> contains tests for the class <code>{@link AccessRight}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AccessRightCpTest {
    /**
     * Run the AccessRight(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAccessRight_1()
            throws Exception {
        AccessRight result = AccessRight.CONTROL_JOB;
        assertNotNull(result);
    }

    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        AccessRight fixture = AccessRight.CONTROL_JOB;

        String result = fixture.getDisplay();

        assertEquals("Control Job", result);
    }
}