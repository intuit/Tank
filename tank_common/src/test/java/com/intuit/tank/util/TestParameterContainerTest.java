package com.intuit.tank.util;

/*
 * #%L
 * Common
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

import com.intuit.tank.util.TestParameterContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>TestParameterContainerTest</code> contains tests for the class
 * <code>{@link TestParameterContainer}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:36 AM
 */
public class TestParameterContainerTest {
    /**
     * Run the TestParameterContainer.Builder builder() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:36 AM
     */
    @Test
    public void testBuilder_1()
            throws Exception {

        TestParameterContainer.Builder result = TestParameterContainer.builder();

        assertNotNull(result);
    }
}