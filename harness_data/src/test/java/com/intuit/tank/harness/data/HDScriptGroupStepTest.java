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

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDScriptGroupStep;
import com.intuit.tank.harness.data.HDScriptUseCase;

/**
 * The class <code>HDScriptGroupStepTest</code> contains tests for the class <code>{@link HDScriptGroupStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDScriptGroupStepTest {
    /**
     * Run the HDScriptUseCase getUseCase() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetUseCase_1()
            throws Exception {
        HDScriptGroupStep fixture = new HDScriptGroupStep();
        fixture.setUseCase(new HDScriptUseCase());

        HDScriptUseCase result = fixture.getUseCase();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getParent());
    }

    /**
     * Run the void setUseCase(HDScriptUseCase) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetUseCase_1()
            throws Exception {
        HDScriptGroupStep fixture = new HDScriptGroupStep();
        fixture.setUseCase(new HDScriptUseCase());
        HDScriptUseCase useCase = new HDScriptUseCase();

        fixture.setUseCase(useCase);

    }
}