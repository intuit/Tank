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

import com.intuit.tank.filter.FilterGroupEnvelope;
import com.intuit.tank.project.ScriptFilterGroup;

/**
 * The class <code>FilterGroupEnvelopeTest</code> contains tests for the class <code>{@link FilterGroupEnvelope}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class FilterGroupEnvelopeTest {
    /**
     * Run the FilterGroupEnvelope(ScriptFilterGroup) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testFilterGroupEnvelope_1()
            throws Exception {
        ScriptFilterGroup scriptFilterGroup = new ScriptFilterGroup();

        FilterGroupEnvelope result = new FilterGroupEnvelope(scriptFilterGroup);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the ScriptFilterGroup getFilterGroup() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetFilterGroup_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);

        ScriptFilterGroup result = fixture.getFilterGroup();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);
        fixture.setName("NAme");
        String result = fixture.getName();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
        assertNotNull(result);
    }

    /**
     * Run the String getProductName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetProductName_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);
        fixture.setProductName("Test");

        String result = fixture.getProductName();

        assertNotNull(result);
    }

    /**
     * Run the boolean isChecked() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsChecked_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);

        boolean result = fixture.isChecked();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
        assertTrue(result);
    }

    /**
     * Run the boolean isChecked() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsChecked_2()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);

        boolean result = fixture.isChecked();
        assertTrue(result);
    }

    /**
     * Run the void setChecked(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetChecked_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);
        boolean checked = true;

        fixture.setChecked(checked);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
    }

    /**
     * Run the void setFilterGroup(ScriptFilterGroup) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetFilterGroup_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);
        ScriptFilterGroup filter = new ScriptFilterGroup();

        fixture.setFilterGroup(filter);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);
        String name = "";

        fixture.setName(name);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
    }

    /**
     * Run the void setProductName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetProductName_1()
            throws Exception {
        FilterGroupEnvelope fixture = new FilterGroupEnvelope(new ScriptFilterGroup());
        fixture.setChecked(true);
        String productName = "";

        fixture.setProductName(productName);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.filter.FilterGroupEnvelope.<init>(FilterGroupEnvelope.java:15)
    }
}