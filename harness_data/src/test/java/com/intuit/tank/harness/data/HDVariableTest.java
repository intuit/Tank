package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDVariable;

/**
 * The class <code>HDVariableTest</code> contains tests for the class <code>{@link HDVariable}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDVariableTest {
    /**
     * Run the HDVariable() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDVariable_1()
            throws Exception {

        HDVariable result = new HDVariable();

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
    }

    /**
     * Run the HDVariable(String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDVariable_2()
            throws Exception {
        String key = "";
        String value = "";

        HDVariable result = new HDVariable(key, value);

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the String getKey() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetKey_1()
            throws Exception {
        HDVariable fixture = new HDVariable("", "");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        HDVariable fixture = new HDVariable("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setKey(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetKey_1()
            throws Exception {
        HDVariable fixture = new HDVariable("", "");
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetValue_1()
            throws Exception {
        HDVariable fixture = new HDVariable("", "");
        String value = "";

        fixture.setValue(value);

    }
}