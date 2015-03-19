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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterAction;
import com.intuit.tank.project.ScriptFilterCondition;
import com.intuit.tank.util.ScriptFilterType;

/**
 * The class <code>ScriptFilterTest</code> contains tests for the class <code>{@link ScriptFilter}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class ScriptFilterTest {
    /**
     * Run the ScriptFilter() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testScriptFilter_1()
        throws Exception {
        ScriptFilter result = new ScriptFilter();
        assertNotNull(result);
    }

    /**
     * Run the void addAction(ScriptFilterAction) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddAction_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        ScriptFilterAction action = new ScriptFilterAction();

        fixture.addAction(action);

    }

    /**
     * Run the void addCondition(ScriptFilterCondition) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testAddCondition_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        ScriptFilterCondition condition = new ScriptFilterCondition();

        fixture.addCondition(condition);

    }

    /**
     * Run the int compareTo(ScriptFilter) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testCompareTo_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        ScriptFilter o = new ScriptFilter();
        o.setName("");

        int result = fixture.compareTo(o);
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
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
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
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        Object obj = new ScriptFilter();

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
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        Object obj = new ScriptFilter();

        boolean result = fixture.equals(obj);

        assertEquals(true, result);
    }

    /**
     * Run the Set<ScriptFilterAction> getActions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetActions_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        Set<ScriptFilterAction> result = fixture.getActions();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the boolean getAllConditionsMustPass() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetAllConditionsMustPass_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        boolean result = fixture.getAllConditionsMustPass();

        assertEquals(true, result);
    }

    /**
     * Run the boolean getAllConditionsMustPass() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetAllConditionsMustPass_2()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(false);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        boolean result = fixture.getAllConditionsMustPass();

        assertEquals(false, result);
    }

    /**
     * Run the Set<ScriptFilterCondition> getConditions() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetConditions_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        Set<ScriptFilterCondition> result = fixture.getConditions();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Integer getExternalScriptId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetExternalScriptId_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        Integer result = fixture.getExternalScriptId();

        assertNotNull(result);
        assertEquals("1", result.toString());
        assertEquals((byte) 1, result.byteValue());
        assertEquals((short) 1, result.shortValue());
        assertEquals(1, result.intValue());
        assertEquals(1L, result.longValue());
        assertEquals(1.0f, result.floatValue(), 1.0f);
        assertEquals(1.0, result.doubleValue(), 1.0);
    }

    /**
     * Run the ScriptFilterType getFilterType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetFilterType_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType((ScriptFilterType) null);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        ScriptFilterType result = fixture.getFilterType();

        assertNotNull(result);
        assertEquals("Internal Filter", result.getDisplay());
        assertEquals("INTERNAL", result.name());
        assertEquals("INTERNAL", result.toString());
        assertEquals(0, result.ordinal());
    }

    /**
     * Run the ScriptFilterType getFilterType() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetFilterType_2()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        ScriptFilterType result = fixture.getFilterType();

        assertNotNull(result);
        assertEquals("External Filter", result.getDisplay());
        assertEquals("EXTERNAL", result.name());
        assertEquals("EXTERNAL", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the boolean getPersist() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPersist_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        boolean result = fixture.getPersist();

        assertEquals(true, result);
    }

    /**
     * Run the boolean getPersist() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetPersist_2()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(false);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        boolean result = fixture.getPersist();

        assertEquals(false, result);
    }

    /**
     * Run the String getProductName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetProductName_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        String result = fixture.getProductName();

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
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        int result = fixture.hashCode();

        assertEquals(1599, result);
    }

    /**
     * Run the void setActions(Set<ScriptFilterAction>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetActions_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        Set<ScriptFilterAction> actions = new HashSet();

        fixture.setActions(actions);

    }

    /**
     * Run the void setAllConditionsMustPass(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetAllConditionsMustPass_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        boolean allConditionsMustPass = true;

        fixture.setAllConditionsMustPass(allConditionsMustPass);

    }

    /**
     * Run the void setConditions(Set<ScriptFilterCondition>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetConditions_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        Set<ScriptFilterCondition> conditions = new HashSet();

        fixture.setConditions(conditions);

    }

    /**
     * Run the void setExternalScriptId(Integer) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetExternalScriptId_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        Integer externalScriptId = new Integer(1);

        fixture.setExternalScriptId(externalScriptId);

    }

    /**
     * Run the void setFilterType(ScriptFilterType) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetFilterType_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        ScriptFilterType filterType = ScriptFilterType.EXTERNAL;

        fixture.setFilterType(filterType);

    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setPersist(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetPersist_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        boolean persist = true;

        fixture.setPersist(persist);

    }

    /**
     * Run the void setProductName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetProductName_1()
        throws Exception {
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());
        String productName = "";

        fixture.setProductName(productName);

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
        ScriptFilter fixture = new ScriptFilter();
        fixture.setActions(new HashSet());
        fixture.setExternalScriptId(new Integer(1));
        fixture.setPersist(true);
        fixture.setAllConditionsMustPass(true);
        fixture.setName("");
        fixture.setFilterType(ScriptFilterType.EXTERNAL);
        fixture.setProductName("");
        fixture.setConditions(new HashSet());

        String result = fixture.toString();

    }
}