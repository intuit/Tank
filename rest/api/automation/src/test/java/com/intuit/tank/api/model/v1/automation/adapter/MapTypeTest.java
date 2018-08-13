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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.*;

import com.intuit.tank.api.model.v1.automation.adapter.MapEntryType;
import com.intuit.tank.api.model.v1.automation.adapter.MapType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>MapTypeTest</code> contains tests for the class <code>{@link MapType}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:50 PM
 */
public class MapTypeTest {
    /**
     * Run the MapType() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMapType_1()
        throws Exception {

        MapType result = new MapType();

        assertNotNull(result);
    }

    /**
     * Run the MapType(Map<String,String>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMapType_2()
        throws Exception {
        Map<String, String> map = new HashMap();

        MapType result = new MapType(map);

        assertNotNull(result);
    }

    /**
     * Run the MapType(Map<String,String>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMapType_3()
        throws Exception {
        Map<String, String> map = new HashMap();

        MapType result = new MapType(map);

        assertNotNull(result);
    }

    /**
     * Run the List<MapEntryType> getEntry() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testGetEntry_1()
        throws Exception {
        MapType fixture = new MapType();
        fixture.setEntry(new LinkedList());

        List<MapEntryType> result = fixture.getEntry();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setEntry(List<MapEntryType>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testSetEntry_1()
        throws Exception {
        MapType fixture = new MapType();
        fixture.setEntry(new LinkedList());
        List<MapEntryType> entry = new LinkedList();

        fixture.setEntry(entry);

    }
}