package com.intuit.tank.filter;

/*
 * #%L
 * JSF Support Beans
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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.filter.ScriptfilterEnvelope;
import com.intuit.tank.project.ScriptFilter;

/**
 * The class <code>ScriptfilterEnvelopeTest</code> contains tests for the class
 * <code>{@link ScriptfilterEnvelope}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ScriptfilterEnvelopeTest {
    /**
     * Run the ScriptfilterEnvelope(ScriptFilter) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testScriptfilterEnvelope_1()
            throws Exception {
        ScriptFilter scriptFilter = new ScriptFilter();

        ScriptfilterEnvelope result = new ScriptfilterEnvelope(scriptFilter);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
        assertNotNull(result);
    }

    /**
     * Run the ScriptFilter getFilter() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetFilter_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);

        ScriptFilter result = fixture.getFilter();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);
        fixture.setName("name");
        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
        assertNotNull(result);
    }

    /**
     * Run the String getProductName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetProductName_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);
        fixture.setProductName("blah:");
        String result = fixture.getProductName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
        assertNotNull(result);
    }

    /**
     * Run the boolean isChecked() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsChecked_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);

        boolean result = fixture.isChecked();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
        assertTrue(result);
    }

    /**
     * Run the boolean isChecked() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsChecked_2()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(false);

        boolean result = fixture.isChecked();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
        assertTrue(!result);
    }

    /**
     * Run the void setChecked(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetChecked_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);
        boolean checked = true;

        fixture.setChecked(checked);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
    }

    /**
     * Run the void setFilter(ScriptFilter) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetFilter_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);
        ScriptFilter filter = new ScriptFilter();

        fixture.setFilter(filter);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
    }

    /**
     * Run the void setProductName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetProductName_1()
            throws Exception {
        ScriptfilterEnvelope fixture = new ScriptfilterEnvelope(new ScriptFilter());
        fixture.setChecked(true);
        String productName = "";

        fixture.setProductName(productName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.ScriptfilterEnvelope.<init>(ScriptfilterEnvelope.java:16)
    }
}