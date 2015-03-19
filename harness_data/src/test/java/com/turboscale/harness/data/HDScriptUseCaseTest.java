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
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.intuit.tank.harness.data.HDScript;
import com.intuit.tank.harness.data.HDScriptUseCase;
import com.intuit.tank.harness.data.TestStep;

/**
 * The class <code>HDScriptUseCaseTest</code> contains tests for the class <code>{@link HDScriptUseCase}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDScriptUseCaseTest {
    /**
     * Run the HDScriptUseCase() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDScriptUseCase_1()
            throws Exception {
        HDScriptUseCase result = new HDScriptUseCase();
        assertNotNull(result);
    }

    /**
     * Run the String getName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetName_1()
            throws Exception {
        HDScriptUseCase fixture = new HDScriptUseCase();
        fixture.setName("");
        fixture.setParent(new HDScript());

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the HDScript getParent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetParent_1()
            throws Exception {
        HDScriptUseCase fixture = new HDScriptUseCase();
        fixture.setName("");
        fixture.setParent(new HDScript());

        HDScript result = fixture.getParent();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getParent());
        assertEquals(0, result.getLoop());
    }

    /**
     * Run the List<TestStep> getScriptSteps() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetScriptSteps_1()
            throws Exception {
        HDScriptUseCase fixture = new HDScriptUseCase();
        fixture.setName("");
        fixture.setParent(new HDScript());

        List<TestStep> result = fixture.getScriptSteps();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the void setName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetName_1()
            throws Exception {
        HDScriptUseCase fixture = new HDScriptUseCase();
        fixture.setName("");
        fixture.setParent(new HDScript());
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setParent(HDScript) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetParent_1()
            throws Exception {
        HDScriptUseCase fixture = new HDScriptUseCase();
        fixture.setName("");
        fixture.setParent(new HDScript());
        HDScript parent = new HDScript();

        fixture.setParent(parent);

    }
}