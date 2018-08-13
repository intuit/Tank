package com.intuit.tank.vmManager.environment.amazon;

/*
 * #%L
 * VmManager
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

import com.intuit.tank.vmManager.environment.amazon.KeyValuePair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>KeyValuePairTest</code> contains tests for the class <code>{@link KeyValuePair}</code>.
 *
 * @generatedBy CodePro at 12/16/14 6:30 PM
 */
public class KeyValuePairTest {
    /**
     * Run the KeyValuePair(String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testKeyValuePair_1()
        throws Exception {
        String key = "";
        String value = "";

        KeyValuePair result = new KeyValuePair(key, value);

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        KeyValuePair fixture = new KeyValuePair("", "");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        KeyValuePair fixture = new KeyValuePair("", "");

        String result = fixture.getValue();

        assertEquals("", result);
    }
}