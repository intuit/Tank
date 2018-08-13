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

import com.intuit.tank.api.model.v1.automation.adapter.MapType;
import com.intuit.tank.api.model.v1.automation.adapter.XmlGenericMapAdapter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>XmlGenericMapAdapterTest</code> contains tests for the class <code>{@link XmlGenericMapAdapter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 2:50 PM
 */
public class XmlGenericMapAdapterTest {
    /**
     * Run the MapType marshal(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMarshal_1()
        throws Exception {
        XmlGenericMapAdapter fixture = new XmlGenericMapAdapter();
        Map<String, String> v = new HashMap();

        MapType result = fixture.marshal(v);

        assertNotNull(result);
    }

    /**
     * Run the MapType marshal(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMarshal_2()
        throws Exception {
        XmlGenericMapAdapter fixture = new XmlGenericMapAdapter();
        Map<String, String> v = new HashMap();

        MapType result = fixture.marshal(v);

        assertNotNull(result);
    }

    /**
     * Run the MapType marshal(Map<String,String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testMarshal_3()
        throws Exception {
        XmlGenericMapAdapter fixture = new XmlGenericMapAdapter();
        Map<String, String> v = null;

        MapType result = fixture.marshal(v);

        assertNotNull(result);
    }

    /**
     * Run the Map<String, String> unmarshal(MapType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testUnmarshal_1()
        throws Exception {
        XmlGenericMapAdapter fixture = new XmlGenericMapAdapter();
        MapType v = new MapType();
        v.setEntry(new LinkedList());

        Map<String, String> result = fixture.unmarshal(v);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Map<String, String> unmarshal(MapType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testUnmarshal_2()
        throws Exception {
        XmlGenericMapAdapter fixture = new XmlGenericMapAdapter();
        MapType v = new MapType();
        v.setEntry(new LinkedList());

        Map<String, String> result = fixture.unmarshal(v);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Map<String, String> unmarshal(MapType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 2:50 PM
     */
    @Test
    public void testUnmarshal_3()
        throws Exception {
        XmlGenericMapAdapter fixture = new XmlGenericMapAdapter();
        MapType v = null;

        Map<String, String> result = fixture.unmarshal(v);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}