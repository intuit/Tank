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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.script.ScriptEngine;

import org.junit.jupiter.api.Test;

import com.intuit.tank.project.ExternalScript;

/**
 * The class <code>ExternalScriptTest</code> contains tests for the class <code>{@link ExternalScript}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ExternalScriptTest {
    /**
     * Run the ExternalScript() constructor test.
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testExternalScript_1()
            throws Exception {
        ExternalScript result = new ExternalScript();
        assertNotNull(result);
    }

    /**
     * Run the int compareTo(ExternalScript) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
        ExternalScript o = new ExternalScript();
        o.setName("");
        int result = fixture.compareTo(o);
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
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
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
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
        Object obj = new ExternalScript();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
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
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
        Object obj = new ExternalScript();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the ScriptEngine getEngine() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetEngine_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");

        ScriptEngine result = fixture.getEngine();

        assertEquals(null, result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getProductName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProductName_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");

        String result = fixture.getProductName();

        assertEquals("", result);
    }

    /**
     * Run the String getScript() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScript_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");

        String result = fixture.getScript();

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
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");

        int result = fixture.hashCode();

        assertEquals(1599, result);
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setProductName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProductName_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
        String productName = "";

        fixture.setProductName(productName);

    }

    /**
     * Run the void setScript(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScript_1()
            throws Exception {
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");
        String script = "";

        fixture.setScript(script);

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
        ExternalScript fixture = new ExternalScript();
        fixture.setScript("");
        fixture.setProductName("");
        fixture.setName("");

        String result = fixture.toString();

    }
}