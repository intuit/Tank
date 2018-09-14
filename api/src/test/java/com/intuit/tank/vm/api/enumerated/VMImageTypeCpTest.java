package com.intuit.tank.vm.api.enumerated;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.api.enumerated.VMImageType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>VMImageTypeCpTest</code> contains tests for the class <code>{@link VMImageType}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class VMImageTypeCpTest {
    /**
     * Run the String getConfigName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetConfigName_1()
            throws Exception {
        VMImageType fixture = VMImageType.AGENT;

        String result = fixture.getConfigName();

        assertEquals("Agent", result);
    }
}