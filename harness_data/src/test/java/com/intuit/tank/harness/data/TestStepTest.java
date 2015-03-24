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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.TestStep;

/**
 * The class <code>TestStepTest</code> contains tests for the class <code>{@link TestStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class TestStepTest {
    /**
     * Run the HDScriptUseCase getParent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetParent_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.stepIndex = 1;

        HDScriptUseCase result = fixture.getParent();

        assertEquals(null, result);
    }

    /**
     * Run the int getStepIndex() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetStepIndex_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.stepIndex = 1;

        int result = fixture.getStepIndex();

        assertEquals(1, result);
    }

    /**
     * Run the void setParent(HDScriptUseCase) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetParent_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.stepIndex = 1;
        HDScriptUseCase parent = new HDScriptUseCase();

        fixture.setParent(parent);

    }

    /**
     * Run the void setStepIndex(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetStepIndex_1()
            throws Exception {
        ClearCookiesStep fixture = new ClearCookiesStep();
        fixture.stepIndex = 1;
        int stepIndex = 1;

        fixture.setStepIndex(stepIndex);

    }
}