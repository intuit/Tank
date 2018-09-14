package com.intuit.tank.vm.settings;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.jupiter.api.*;

import com.intuit.tank.vm.settings.LogicStepConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>LogicStepConfigCpTest</code> contains tests for the class <code>{@link LogicStepConfig}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class LogicStepConfigCpTest {
    /**
     * Run the LogicStepConfig(HierarchicalConfiguration) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testLogicStepConfig_1()
            throws Exception {
        HierarchicalConfiguration config = new HierarchicalConfiguration();

        LogicStepConfig result = new LogicStepConfig(config);

        assertNotNull(result);
        assertEquals("", result.getInsertBefore());
        assertEquals("", result.getAppendAfter());
    }

    /**
     * Run the String getAppendAfter() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetAppendAfter_1()
            throws Exception {
        LogicStepConfig fixture = new LogicStepConfig(new HierarchicalConfiguration());

        String result = fixture.getAppendAfter();

        assertEquals("", result);
    }

    /**
     * Run the String getInsertBefore() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetInsertBefore_1()
            throws Exception {
        LogicStepConfig fixture = new LogicStepConfig(new HierarchicalConfiguration());

        String result = fixture.getInsertBefore();

        assertEquals("", result);
    }
}