package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
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

import com.intuit.tank.api.model.v1.script.StepDataTO;

import static org.junit.Assert.*;

/**
 * The class <code>StepDataTOTest</code> contains tests for the class <code>{@link StepDataTO}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:09 PM
 */
public class StepDataTOTest {
    /**
     * Run the StepDataTO() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testStepDataTO_1()
        throws Exception {

        StepDataTO result = new StepDataTO();

        assertNotNull(result);
        assertEquals(null, result.getValue());
        assertEquals(null, result.getKey());
        assertEquals(null, result.getType());
        assertEquals(null, result.getPhase());
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");

        String result = fixture.getKey();

        assertEquals("", result);
    }

    /**
     * Run the String getPhase() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetPhase_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");

        String result = fixture.getPhase();

        assertEquals("", result);
    }

    /**
     * Run the String getType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetType_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");

        String result = fixture.getType();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");
        String key = "";

        fixture.setKey(key);

    }

    /**
     * Run the void setPhase(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetPhase_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");
        String phase = "";

        fixture.setPhase(phase);

    }

    /**
     * Run the void setType(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetType_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");
        String type = "";

        fixture.setType(type);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:09 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        StepDataTO fixture = new StepDataTO();
        fixture.setValue("");
        fixture.setKey("");
        fixture.setType("");
        fixture.setPhase("");
        String value = "";

        fixture.setValue(value);

    }
}