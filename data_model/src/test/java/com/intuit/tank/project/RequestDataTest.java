package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
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

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.RequestDataPhase;

/**
 * The class <code>RequestDataTest</code> contains tests for the class <code>{@link RequestData}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class RequestDataTest {
    /**
     * Run the RequestData() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testRequestData_1()
        throws Exception {

        RequestData result = new RequestData();

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
        assertEquals(null, result.getType());
    }

    /**
     * Run the RequestData(String,String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testRequestData_2()
        throws Exception {
        String key = "";
        String value = "";
        String type = "";

        RequestData result = new RequestData(key, value, type);

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("", result.getKey());
        assertEquals("", result.getType());
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        Object obj = new RequestData();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        Object obj = new RequestData();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the RequestDataPhase getPhase() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPhase_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase((RequestDataPhase) null);

        RequestDataPhase result = fixture.getPhase();

        assertNotNull(result);
        assertEquals("Post Request", result.getDisplay());
        assertEquals("POST_REQUEST", result.name());
        assertEquals("POST_REQUEST", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the RequestDataPhase getPhase() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPhase_2()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        RequestDataPhase result = fixture.getPhase();

        assertNotNull(result);
        assertEquals("Post Request", result.getDisplay());
        assertEquals("POST_REQUEST", result.name());
        assertEquals("POST_REQUEST", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        String result = fixture.getType();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        int result = fixture.hashCode();

    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setPhase(RequestDataPhase) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPhase_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        RequestDataPhase phase = RequestDataPhase.POST_REQUEST;

        fixture.setPhase(phase);

    }

    /**
     * Run the void setType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        String type = "";

        fixture.setType(type);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        String value = "";

        fixture.setValue(value);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        RequestData fixture = new RequestData("", "", "");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        String result = fixture.toString();

    }
}