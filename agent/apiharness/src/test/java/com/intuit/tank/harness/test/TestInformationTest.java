package com.intuit.tank.harness.test;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.harness.test.data.Variables;

/**
 * The class <code>TestInformationTest</code> contains tests for the class <code>{@link TestInformation}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 9:20 PM
 */
public class TestInformationTest {
    @Test
    public void testTestInformation_1() {
        TestInformation result = new TestInformation();
        assertNotNull(result);
    }

    @Test
    public void testAddComment_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String comment = "";

        fixture.addComment(comment);
    }

    @Test
    public void testGetComments_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");

        Vector<String> result = fixture.getComments();

        assertNotNull(result);
    }

    @Test
    public void testGetVariables_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");

        HashMap<String, String> result = fixture.getVariables();

        assertNotNull(result);
    }

    @Test
    public void testProcessVariables_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        Variables variables = new Variables();

        fixture.processVariables(variables);

    }

    @Test
    public void testSetAuthenticate_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        boolean value = true;

        fixture.setAuthenticate(value);
    }

    @Test
    public void testGetLoggingKey() {
        TestInformation fixture = new TestInformation();
        fixture.setLoggingKey("testLoggingKey");
        assertEquals("testLoggingKey", fixture.getLoggingKey());
    }

    @Test
    public void testGetTestName() {
        TestInformation fixture = new TestInformation();
        fixture.setTestName("testTestName");
        assertEquals("testTestName", fixture.getTestName());
    }

    @Test
    public void testSetDescription_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setDescription(value);
    }

    @Test
    public void testSetHost_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setHost(value);
    }

    @Test
    public void testSetLoggingKey_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setLoggingKey(value);
    }

    @Test
    public void testSetLoop_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        int loop = 1;

        fixture.setLoop(loop);
    }

    @Test
    public void testSetName_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "testName";

        fixture.setName(value);
        assertEquals(value, fixture.getName());
    }

    @Test
    public void testSetPath_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setPath(value);
    }

    @Test
    public void testSetPort_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setPort(value);

    }

    @Test
    public void testSetProtocol_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setProtocol(value);

    }

    @Test
    public void testSetTemplate_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setTemplate(value);
    }

    @Test
    public void testSetTestName_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String value = "";

        fixture.setTestName(value);
    }

    @Test
    public void testSetVariable_1() {
        TestInformation fixture = new TestInformation();
        fixture.addComment("");
        String key = "";
        String value = "";

        fixture.setVariable(key, value);
    }

    @Test
    public void testGetVariable_1() {
        TestInformation fixture = new TestInformation();
        fixture.setVariable("testKey", "testValue");
        assertEquals("testValue", fixture.getVariable("testKey"));
    }

    @Test
    public void testGetDescription() {
        TestInformation fixture = new TestInformation();
        fixture.setDescription("testDescription");
        assertEquals("testDescription", fixture.getDescription());
    }

    @Test
    public void testGetProtocol() {
        TestInformation fixture = new TestInformation();
        fixture.setProtocol("testProtocol");
        assertEquals("testProtocol", fixture.getProtocol());
    }

    @Test
    public void testGetHost() {
        TestInformation fixture = new TestInformation();
        fixture.setHost("testHost");
        assertEquals("testHost", fixture.getHost());
    }

    @Test
    public void testGetPath() {
        TestInformation fixture = new TestInformation();
        fixture.setPath("testPath");
        assertEquals("testPath", fixture.getPath());
    }

    @Test
    public void testGetPort() {
        TestInformation fixture = new TestInformation();
        fixture.setPort("testPort");
        assertEquals("testPort", fixture.getPort());
    }

    @Test
    public void testGetLoop() {
        TestInformation fixture = new TestInformation();
        fixture.setLoop(12);
        assertEquals(12, fixture.getLoop());
    }

    @Test
    public void testGetTemplate() {
        TestInformation fixture = new TestInformation();
        fixture.setTemplate("testTemplate");
        assertEquals("testTemplate", fixture.getTemplate());
    }

    @Test
    public void testAuthenticate() {
        TestInformation fixture = new TestInformation();
        assertFalse(fixture.authenticate());
        fixture.setAuthenticate(true);
        assertTrue(fixture.authenticate());
    }

    @Test
    public void testProcessVariables() {
        TestInformation fixture = new TestInformation();
        Variables variables = new Variables();
        fixture.setVariable("key", "#function.date.currentDate.06-01-2011");
        fixture.processVariables(variables);
        Map<String, String> expected = new HashMap<>();
        expected.put("key", "06-01-2011");
        assertEquals(expected, variables.getVariableValues());
    }
}
