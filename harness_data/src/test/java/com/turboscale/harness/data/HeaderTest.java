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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.harness.data.Header;

/**
 * The class <code>HeaderTest</code> contains tests for the class <code>{@link Header}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HeaderTest {
    /**
     * Run the Header() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHeader_1()
            throws Exception {
        Header result = new Header();
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testEquals_1()
            throws Exception {
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testEquals_2()
            throws Exception {
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");
        Object obj = new Header();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testEquals_3()
            throws Exception {
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");
        Object obj = new Header();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the String getAction() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetAction_1()
            throws Exception {
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");

        String result = fixture.getAction();

        assertEquals("", result);
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
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");

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
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHashCode_1()
            throws Exception {
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");

        int result = fixture.hashCode();

        assertEquals(1305, result);
    }

    /**
     * Run the void setAction(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetAction_1()
            throws Exception {
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");
        String action = "";

        fixture.setAction(action);

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
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");
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
        Header fixture = new Header();
        fixture.setAction("");
        fixture.setValue("");
        fixture.setKey("");
        String value = "";

        fixture.setValue(value);

    }
}