package com.intuit.tank.api.model.v1.automation.adapter;

/*
 * #%L
 * Automation Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Map;

import org.apache.commons.collections.DefaultMapEntry;
import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.automation.adapter.MapEntryType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>MapEntryTypeTest</code> contains tests for the class <code>{@link MapEntryType}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:50 PM
 */
public class MapEntryTypeTest {
    /**
     * Run the MapEntryType() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMapEntryType_1()
        throws Exception {

        MapEntryType result = new MapEntryType();

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
    }

    /**
     * Run the MapEntryType(Entry<String,String>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMapEntryType_2()
        throws Exception {
        java.util.Map.Entry<String, String> e = new DefaultMapEntry();

        MapEntryType result = new MapEntryType(e);

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");
        MapEntryType obj = new MapEntryType();
        obj.setKey("");
        obj.setValue("");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");
        MapEntryType obj = new MapEntryType();
        obj.setKey("");
        obj.setValue("");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");

        int result = fixture.hashCode();

        assertEquals(670761, result);
    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        MapEntryType fixture = new MapEntryType();
        fixture.setKey("");
        fixture.setValue("");
        String value = "";

        fixture.setValue(value);

    }
}