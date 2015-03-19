package com.intuit.tank.script.util;

/*
 * #%L
 * Script Processor
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

import static org.junit.Assert.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.script.util.RequestDataBuilder;

/**
 * The class <code>RequestDataBuilderTest</code> contains tests for the class <code>{@link RequestDataBuilder}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:48 PM
 */
public class RequestDataBuilderTest {
    /**
     * Run the RequestDataBuilder(String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testRequestDataBuilder_1()
        throws Exception {
        String type = "";

        RequestDataBuilder result = new RequestDataBuilder(type);

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals("", result.getPath());
    }

    /**
     * Run the void addPathElement(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testAddPathElement_1()
        throws Exception {
        RequestDataBuilder fixture = new RequestDataBuilder("");
        fixture.setValue("");
        String path = "";

        fixture.addPathElement(path);

    }

    /**
     * Run the RequestData build(String,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testBuild_1()
        throws Exception {
        RequestDataBuilder fixture = new RequestDataBuilder("");
        fixture.setValue("");
        String lastElement = "";
        String value = "";

        RequestData result = fixture.build(lastElement, value);

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("/", result.getKey());
        assertEquals("", result.getType());
    }

    /**
     * Run the RequestDataBuilder copy() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testCopy_1()
        throws Exception {
        RequestDataBuilder fixture = new RequestDataBuilder("");
        fixture.setValue("");

        RequestDataBuilder result = fixture.copy();

        assertNotNull(result);
        assertEquals("", result.getValue());
        assertEquals("", result.getPath());
    }

    /**
     * Run the String getPath() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testGetPath_1()
        throws Exception {
        RequestDataBuilder fixture = new RequestDataBuilder("");
        fixture.setValue("");

        String result = fixture.getPath();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        RequestDataBuilder fixture = new RequestDataBuilder("");
        fixture.setValue("");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:48 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        RequestDataBuilder fixture = new RequestDataBuilder("");
        fixture.setValue("");
        String value = "";

        fixture.setValue(value);

    }
}