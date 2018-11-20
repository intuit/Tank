package com.intuit.tank.api.model.v1.project;

/*
 * #%L
 * Project Rest API
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

import com.intuit.tank.api.model.v1.project.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>KeyPairTest</code> contains tests for the class <code>{@link KeyPair}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:08 PM
 */
public class KeyPairTest {
    /**
     * Run the KeyPair() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testKeyPair_1()
        throws Exception {

        KeyPair result = new KeyPair();

        assertNotNull(result);
        assertEquals("null = null", result.toString());
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
    }

    /**
     * Run the KeyPair(String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testKeyPair_2()
        throws Exception {
        String key = "";
        String value = "";

        KeyPair result = new KeyPair(key, value);

        assertNotNull(result);
        assertEquals(" = ", result.toString());
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        KeyPair fixture = new KeyPair("", "");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        KeyPair fixture = new KeyPair("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        KeyPair fixture = new KeyPair("", "");
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        KeyPair fixture = new KeyPair("", "");
        String value = "";

        fixture.setValue(value);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:08 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        KeyPair fixture = new KeyPair("", "");

        String result = fixture.toString();

        assertEquals(" = ", result);
    }
}