package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
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

import com.intuit.tank.project.AssignmentResponseContent;

/**
 * The class <code>AssignmentResponseContentTest</code> contains tests for the class <code>{@link AssignmentResponseContent}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class AssignmentResponseContentTest {
    /**
     * Run the AssignmentResponseContent() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAssignmentResponseContent_1()
        throws Exception {
        AssignmentResponseContent result = new AssignmentResponseContent();
        assertNotNull(result);
    }

    /**
     * Run the String getOperator() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetOperator_1()
        throws Exception {
        AssignmentResponseContent fixture = new AssignmentResponseContent();

        String result = fixture.getOperator();

        assertEquals("=", result);
    }
}