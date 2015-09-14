package com.intuit.tank.vm.script.util;

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

import org.junit.*;

import com.intuit.tank.vm.script.util.AddActionScope;

import static org.junit.Assert.*;

/**
 * The class <code>AddActionScopeCpTest</code> contains tests for the class <code>{@link AddActionScope}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class AddActionScopeCpTest {
    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        AddActionScope fixture = AddActionScope.assignment;

        String result = fixture.getValue();

        assertEquals("assignment", result);
    }
}