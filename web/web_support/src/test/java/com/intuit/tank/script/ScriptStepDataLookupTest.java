package com.intuit.tank.script;

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

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptStepDataLookup;

import java.util.HashSet;
import java.util.Set;

/**
 * The class <code>ScriptStepDataLookupTest</code> contains tests for the class <code>{@link ScriptStepDataLookup}</code>.
 */
public class ScriptStepDataLookupTest {

    @Test
    public void testGetData_ThinkTime_ReturnsRepresentation() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.THINK_TIME);
        Set<RequestData> data = new HashSet<>();
        data.add(new RequestData(ScriptConstants.MIN_TIME, "100", "string"));
        data.add(new RequestData(ScriptConstants.MAX_TIME, "500", "string"));
        step.setData(data);

        String result = ScriptStepDataLookup.getData(step);
        assertNotNull(result);
        assertTrue(result.contains("Think time"));
        assertTrue(result.contains("100"));
        assertTrue(result.contains("500"));
    }

    @Test
    public void testGetData_ThinkTime_EmptyData_ReturnsDefaultString() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.THINK_TIME);
        step.setData(new HashSet<>());

        String result = ScriptStepDataLookup.getData(step);
        assertNotNull(result);
        assertTrue(result.startsWith("Think time"));
    }

    @Test
    public void testGetData_Sleep_ReturnsRepresentation() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.SLEEP);
        Set<RequestData> data = new HashSet<>();
        data.add(new RequestData(ScriptConstants.TIME, "200", "string"));
        step.setData(data);

        String result = ScriptStepDataLookup.getData(step);
        assertNotNull(result);
        assertTrue(result.contains("Sleep time"));
        assertTrue(result.contains("200"));
    }

    @Test
    public void testGetData_Sleep_EmptyData_ReturnsDefaultString() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.SLEEP);
        step.setData(new HashSet<>());

        String result = ScriptStepDataLookup.getData(step);
        assertNotNull(result);
        assertTrue(result.startsWith("Sleep time"));
    }

    @Test
    public void testGetData_Variable_ReturnsRepresentation() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.VARIABLE);
        Set<RequestData> data = new HashSet<>();
        data.add(new RequestData("myVar", "myValue", "string"));
        step.setData(data);

        String result = ScriptStepDataLookup.getData(step);
        assertNotNull(result);
        assertTrue(result.contains("Variable"));
    }

    @Test
    public void testGetData_Variable_EmptyData_ReturnsDefaultString() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.VARIABLE);
        step.setData(new HashSet<>());

        String result = ScriptStepDataLookup.getData(step);
        assertNotNull(result);
        assertTrue(result.startsWith("Variable"));
    }

    @Test
    public void testGetData_Request_ReturnsHostname() {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.REQUEST);
        step.setHostname("example.com");
        step.setData(new HashSet<>());

        String result = ScriptStepDataLookup.getData(step);
        assertEquals("example.com", result);
    }

    @Test
    public void testGetData_UnknownType_ThrowsNotImplementedException() {
        ScriptStep step = new ScriptStep();
        step.setType("unknownType");

        assertThrows(NotImplementedException.class, () -> ScriptStepDataLookup.getData(step));
    }

    @Test
    public void testGetData_1() throws Exception {
        ScriptStep scriptStep = new ScriptStep();
        scriptStep.setType(ScriptConstants.REQUEST);
        scriptStep.setData(new HashSet<>());
        // Should not throw for a recognized type
        assertDoesNotThrow(() -> ScriptStepDataLookup.getData(scriptStep));
    }
}
