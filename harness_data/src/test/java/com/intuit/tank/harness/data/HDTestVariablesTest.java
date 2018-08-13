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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.data.HDVariable;

/**
 * The class <code>HDTestVariablesTest</code> contains tests for the class <code>{@link HDTestVariables}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDTestVariablesTest {
    /**
     * Run the HDTestVariables() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDTestVariables_1()
            throws Exception {

        HDTestVariables result = new HDTestVariables();

        assertNotNull(result);
        assertEquals(false, result.isAllowOverride());
    }

    /**
     * Run the HDTestVariables(boolean) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDTestVariables_2()
            throws Exception {
        boolean allowOverride = true;

        HDTestVariables result = new HDTestVariables(allowOverride);

        assertNotNull(result);
        assertEquals(true, result.isAllowOverride());
    }

    /**
     * Run the HDTestVariables(boolean,Map<String,String>) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDTestVariables_3()
            throws Exception {
        boolean allowOverride = true;
        Map<String, String> vars = new HashMap();

        HDTestVariables result = new HDTestVariables(allowOverride, vars);

        assertNotNull(result);
        assertEquals(true, result.isAllowOverride());
    }

    /**
     * Run the void addVariable(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testAddVariable_1()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());
        String key = "";
        String value = "";

        fixture.addVariable(key, value);

    }

    /**
     * Run the void addVariables(Map<String,String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testAddVariables_1()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());
        Map<String, String> vars = new HashMap();

        fixture.addVariables(vars);

    }

    /**
     * Run the void addVariables(Map<String,String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testAddVariables_2()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());
        Map<String, String> vars = new HashMap();

        fixture.addVariables(vars);

    }

    /**
     * Run the List<HDVariable> getVariables() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetVariables_1()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());

        List<HDVariable> result = fixture.getVariables();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the boolean isAllowOverride() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testIsAllowOverride_1()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());

        boolean result = fixture.isAllowOverride();

        assertEquals(true, result);
    }

    /**
     * Run the boolean isAllowOverride() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testIsAllowOverride_2()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(false);
        fixture.setVariables(new LinkedList());

        boolean result = fixture.isAllowOverride();

        assertEquals(false, result);
    }

    /**
     * Run the void setAllowOverride(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetAllowOverride_1()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());
        boolean allowOverride = true;

        fixture.setAllowOverride(allowOverride);

    }

    /**
     * Run the void setVariables(List<HDVariable>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetVariables_1()
            throws Exception {
        HDTestVariables fixture = new HDTestVariables(true);
        fixture.setVariables(new LinkedList());
        List<HDVariable> variables = new LinkedList();

        fixture.setVariables(variables);

    }
}