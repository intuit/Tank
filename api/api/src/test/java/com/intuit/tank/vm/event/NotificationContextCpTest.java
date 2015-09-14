package com.intuit.tank.vm.event;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Map;

import org.junit.*;

import com.intuit.tank.vm.event.NotificationContext;

import static org.junit.Assert.*;

/**
 * The class <code>NotificationContextCpTest</code> contains tests for the class
 * <code>{@link NotificationContext}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class NotificationContextCpTest {
    /**
     * Run the NotificationContext() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testNotificationContext_1()
            throws Exception {
        NotificationContext result = new NotificationContext();
        assertNotNull(result);
    }

    /**
     * Run the NotificationContext addContextEntry(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAddContextEntry_1()
            throws Exception {
        NotificationContext fixture = new NotificationContext();
        String key = "";
        String value = "";

        NotificationContext result = fixture.addContextEntry(key, value);

        assertNotNull(result);
        assertEquals("{=N/A}", result.toString());
    }

    /**
     * Run the NotificationContext addContextEntry(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAddContextEntry_2()
            throws Exception {
        NotificationContext fixture = new NotificationContext();
        String key = "";
        String value = "";

        NotificationContext result = fixture.addContextEntry(key, value);

        assertNotNull(result);
        assertEquals("{=N/A}", result.toString());
    }

    /**
     * Run the Map<String, String> getContext() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetContext_1()
            throws Exception {
        NotificationContext fixture = new NotificationContext();

        Map<String, String> result = fixture.getContext();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String replaceValues(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReplaceValues_1()
            throws Exception {
        NotificationContext fixture = new NotificationContext();
        String content = "";

        String result = fixture.replaceValues(content);

        assertEquals("", result);
    }

    /**
     * Run the String replaceValues(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testReplaceValues_2()
            throws Exception {
        NotificationContext fixture = new NotificationContext();
        String content = "";

        String result = fixture.replaceValues(content);

        assertEquals("", result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        NotificationContext fixture = new NotificationContext();

        String result = fixture.toString();

        assertEquals("{}", result);
    }
}