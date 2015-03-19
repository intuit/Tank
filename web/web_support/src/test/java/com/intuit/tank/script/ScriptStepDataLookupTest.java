package com.intuit.tank.script;

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

import static org.junit.Assert.*;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptStepDataLookup;

/**
 * The class <code>ScriptStepDataLookupTest</code> contains tests for the class <code>{@link ScriptStepDataLookup}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class ScriptStepDataLookupTest {
    /**
     * Run the String getData(ScriptStep) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetData_1()
        throws Exception {
        ScriptStep scriptStep = new ScriptStep();

        String result = ScriptStepDataLookup.getData(scriptStep);
    }
}