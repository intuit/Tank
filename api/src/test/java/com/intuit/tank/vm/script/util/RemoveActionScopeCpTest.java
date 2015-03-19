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

import com.intuit.tank.vm.script.util.RemoveActionScope;

import static org.junit.Assert.*;

/**
 * The class <code>RemoveActionScopeCpTest</code> contains tests for the class <code>{@link RemoveActionScope}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class RemoveActionScopeCpTest {
    /**
     * Run the String getValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetValue_1()
            throws Exception {
        RemoveActionScope fixture = RemoveActionScope.postData;

        String result = fixture.getValue();

        assertEquals("postData", result);
    }
}