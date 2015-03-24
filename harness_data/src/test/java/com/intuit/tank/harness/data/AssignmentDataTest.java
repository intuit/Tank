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

import org.junit.Test;

import com.intuit.tank.harness.data.AssignmentData;
import com.intuit.tank.script.RequestDataPhase;

/**
 * The class <code>AssignmentDataTest</code> contains tests for the class <code>{@link AssignmentData}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class AssignmentDataTest {
    /**
     * Run the RequestDataPhase getPhase() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPhase_1()
            throws Exception {
        AssignmentData fixture = new AssignmentData();
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        RequestDataPhase result = fixture.getPhase();

        assertNotNull(result);
        assertEquals("Post Request", result.getDisplay());
        assertEquals("POST_REQUEST", result.name());
        assertEquals("POST_REQUEST", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the void setPhase(RequestDataPhase) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetPhase_1()
            throws Exception {
        AssignmentData fixture = new AssignmentData();
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        RequestDataPhase phase = RequestDataPhase.POST_REQUEST;

        fixture.setPhase(phase);

    }
}