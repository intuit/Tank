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

import com.intuit.tank.harness.data.AssignmentData;
import com.intuit.tank.harness.data.HDAssignment;

/**
 * The class <code>HDAssignmentTest</code> contains tests for the class <code>{@link HDAssignment}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class HDAssignmentTest {
    /**
     * Run the HDAssignment() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testHDAssignment_1()
            throws Exception {
        HDAssignment result = new HDAssignment();
        assertNotNull(result);
    }

    /**
     * Run the List<AssignmentData> getBodyVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetBodyVariable_1()
            throws Exception {
        HDAssignment fixture = new HDAssignment();

        List<AssignmentData> result = fixture.getBodyVariable();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<AssignmentData> getCookieVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCookieVariable_1()
            throws Exception {
        HDAssignment fixture = new HDAssignment();

        List<AssignmentData> result = fixture.getCookieVariable();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<AssignmentData> getHeaderVariable() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetHeaderVariable_1()
            throws Exception {
        HDAssignment fixture = new HDAssignment();

        List<AssignmentData> result = fixture.getHeaderVariable();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}