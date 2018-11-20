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

import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.script.RequestDataPhase;

/**
 * The class <code>ValidationDataTest</code> contains tests for the class <code>{@link ValidationData}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class ValidationDataTest {
    /**
     * Run the ValidationData copy() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testCopy_1()
            throws Exception {
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        ValidationData result = fixture.copy();

        assertNotNull(result);
        assertEquals("null  null", result.toString());
        assertEquals("", result.getCondition());
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
    }

    /**
     * Run the String getCondition() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCondition_1()
            throws Exception {
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        String result = fixture.getCondition();

        assertEquals("", result);
    }

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
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase((RequestDataPhase) null);

        RequestDataPhase result = fixture.getPhase();

        assertNotNull(result);
        assertEquals("Post Request", result.getDisplay());
        assertEquals("POST_REQUEST", result.name());
        assertEquals("POST_REQUEST", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the RequestDataPhase getPhase() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetPhase_2()
            throws Exception {
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        RequestDataPhase result = fixture.getPhase();

        assertNotNull(result);
        assertEquals("Post Request", result.getDisplay());
        assertEquals("POST_REQUEST", result.name());
        assertEquals("POST_REQUEST", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the void setCondition(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetCondition_1()
            throws Exception {
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        String condition = "";

        fixture.setCondition(condition);

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
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);
        RequestDataPhase phase = RequestDataPhase.POST_REQUEST;

        fixture.setPhase(phase);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        ValidationData fixture = new ValidationData();
        fixture.setCondition("");
        fixture.setPhase(RequestDataPhase.POST_REQUEST);

        String result = fixture.toString();

        assertEquals("null  null", result);
    }
}