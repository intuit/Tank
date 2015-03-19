package com.intuit.tank.converter;

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

import org.junit.*;

import com.intuit.tank.converter.ScriptStepHolder;

import static org.junit.Assert.*;

/**
 * The class <code>ScriptStepHolderTest</code> contains tests for the class <code>{@link ScriptStepHolder}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ScriptStepHolderTest {
    /**
     * Run the ScriptStepHolder(int,String,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testScriptStepHolder_1()
            throws Exception {
        int index = 1;
        String uuid = "";
        String label = "";

        ScriptStepHolder result = new ScriptStepHolder(index, uuid, label);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.converter.ScriptStepHolder.<init>(ScriptStepHolder.java:34)
        assertNotNull(result);
    }

    /**
     * Run the int getIndex() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetIndex_1()
            throws Exception {
        ScriptStepHolder fixture = new ScriptStepHolder(1, "", "");

        int result = fixture.getIndex();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.converter.ScriptStepHolder.<init>(ScriptStepHolder.java:34)
        assertEquals(1, result);
    }

    /**
     * Run the String getLabel() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetLabel_1()
            throws Exception {
        ScriptStepHolder fixture = new ScriptStepHolder(1, "", "");

        String result = fixture.getLabel();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.converter.ScriptStepHolder.<init>(ScriptStepHolder.java:34)
        assertNotNull(result);
    }

    /**
     * Run the String getUuid() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetUuid_1()
            throws Exception {
        ScriptStepHolder fixture = new ScriptStepHolder(1, "", "");

        String result = fixture.getUuid();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.converter.ScriptStepHolder.<init>(ScriptStepHolder.java:34)
        assertNotNull(result);
    }

    /**
     * Run the String toCanonicalForm() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testToCanonicalForm_1()
            throws Exception {
        ScriptStepHolder fixture = new ScriptStepHolder(1, "", "");

        String result = fixture.toCanonicalForm();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.converter.ScriptStepHolder.<init>(ScriptStepHolder.java:34)
        assertNotNull(result);
    }
}