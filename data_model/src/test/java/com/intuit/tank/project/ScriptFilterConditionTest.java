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

import com.intuit.tank.project.ScriptFilterCondition;

/**
 * The class <code>ScriptFilterConditionTest</code> contains tests for the class <code>{@link ScriptFilterCondition}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptFilterConditionTest {
    /**
     * Run the ScriptFilterCondition() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptFilterCondition_1()
        throws Exception {
        ScriptFilterCondition result = new ScriptFilterCondition();
        assertNotNull(result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        Object obj = new Object();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_2()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        ScriptFilterCondition obj = new ScriptFilterCondition();
        obj.setCondition("");
        obj.setValue("");
        obj.setScope("");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_3()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        ScriptFilterCondition obj = new ScriptFilterCondition();
        obj.setCondition("");
        obj.setValue("");
        obj.setScope("");

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_4()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        Object obj = new ScriptFilterCondition();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the boolean equals(Object) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testEquals_5()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        Object obj = new ScriptFilterCondition();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the String getCondition() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetCondition_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");

        String result = fixture.getCondition();

        assertEquals("", result);
    }

    /**
     * Run the String getScope() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetScope_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");

        String result = fixture.getScope();

        assertEquals("", result);
    }

    /**
     * Run the String getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");

        String result = fixture.getValue();

        assertEquals("", result);
    }

    /**
     * Run the int hashCode() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testHashCode_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");

        int result = fixture.hashCode();

        assertEquals(627, result);
    }

    /**
     * Run the void setCondition(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetCondition_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        String condition = "";

        fixture.setCondition(condition);

    }

    /**
     * Run the void setScope(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetScope_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        String scope = "";

        fixture.setScope(scope);

    }

    /**
     * Run the void setValue(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");
        String value = "";

        fixture.setValue(value);

    }

    /**
     * Run the String toString() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testToString_1()
        throws Exception {
        ScriptFilterCondition fixture = new ScriptFilterCondition();
        fixture.setCondition("");
        fixture.setValue("");
        fixture.setScope("");

        String result = fixture.toString();

    }
}