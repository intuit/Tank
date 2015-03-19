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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;

/**
 * The class <code>ScriptFilterActionTest</code> contains tests for the class <code>{@link ScriptFilterAction}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptFilterActionTest {
    /**
     * Run the ScriptFilterAction() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptFilterAction_1()
        throws Exception {
        ScriptFilterAction result = new ScriptFilterAction();
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");
        ScriptFilterAction obj = new ScriptFilterAction();
        obj.setKey("");
        obj.setValue("");
        obj.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");
        ScriptFilterAction obj = new ScriptFilterAction();
        obj.setKey("");
        obj.setValue("");
        obj.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");
        Object obj = new ScriptFilterAction();

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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");
        Object obj = new ScriptFilterAction();

        boolean result = fixture.equals(obj);

        assertEquals(false, result);
    }

    /**
     * Run the ScriptFilterActionType getAction() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetAction_1()
        throws Exception {
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");

        ScriptFilterActionType result = fixture.getAction();

        assertNotNull(result);
        assertEquals("add", result.name());
        assertEquals("add", result.toString());
        assertEquals(2, result.ordinal());
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");

        String result = fixture.getKey();

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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");

        int result = fixture.hashCode();

    }

    /**
     * Run the void setAction(ScriptFilterActionType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetAction_1()
        throws Exception {
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");
        ScriptFilterActionType action = ScriptFilterActionType.add;

        fixture.setAction(action);

    }

    /**
     * Run the void setKey(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetKey_1()
        throws Exception {
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");
        String key = "";

        fixture.setKey(key);

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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
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
        ScriptFilterAction fixture = new ScriptFilterAction();
        fixture.setKey("");
        fixture.setValue("");
        fixture.setAction(ScriptFilterActionType.add);
        fixture.setScope("");

        String result = fixture.toString();

    }
}