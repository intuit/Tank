package com.intuit.tank.filter;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.filter.ConditionMatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>ConditionMatchTest</code> contains tests for the class <code>{@link ConditionMatch}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class ConditionMatchTest {
    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        ConditionMatch fixture = ConditionMatch.contains;

        String result = fixture.getValue();

        assertNotNull(result);
    }
}