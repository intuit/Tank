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

import org.junit.*;

import com.intuit.tank.script.FailureTypes;

import static org.junit.Assert.*;

/**
 * The class <code>FailureTypesTest</code> contains tests for the class <code>{@link FailureTypes}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class FailureTypesTest {
    /**
     * Run the String getDisplayName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetDisplayName_1()
        throws Exception {
        FailureTypes fixture = FailureTypes.abortScript;

        String result = fixture.getDisplayName();

        assertNotNull(result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        FailureTypes fixture = FailureTypes.abortScript;

        String result = fixture.getValue();

        assertNotNull(result);
    }

    /**
     * Run the void setDisplayName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetDisplayName_1()
        throws Exception {
        FailureTypes fixture = FailureTypes.abortScript;
        String displayName = "";

        fixture.setDisplayName(displayName);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        FailureTypes fixture = FailureTypes.abortScript;
        String value = "";

        fixture.setValue(value);

    }
}