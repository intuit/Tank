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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;

/**
 * The class <code>JobEventCpTest</code> contains tests for the class <code>{@link JobEvent}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class JobEventCpTest {
    /**
     * Run the JobEvent(String,String,JobLifecycleEvent) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testJobEvent_1()
            throws Exception {
        String jobId = "";
        String message = "";
        JobLifecycleEvent event = JobLifecycleEvent.AGENT_REBOOTED;

        JobEvent result = new JobEvent(jobId, message, event);

        assertNotNull(result);
        assertEquals("", result.getMessage());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the JobEvent addContextEntry(String,String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAddContextEntry_1()
            throws Exception {
        JobEvent fixture = new JobEvent("", "", JobLifecycleEvent.AGENT_REBOOTED);
        String key = "";
        String value = "";

        JobEvent result = fixture.addContextEntry(key, value);

        assertNotNull(result);
        assertEquals("", result.getMessage());
        assertEquals("", result.getJobId());
    }

    /**
     * Run the JobLifecycleEvent getEvent() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetEvent_1()
            throws Exception {
        JobEvent fixture = new JobEvent("", "", JobLifecycleEvent.AGENT_REBOOTED);

        JobLifecycleEvent result = fixture.getEvent();

        assertNotNull(result);
        assertEquals("Agents Rebooted", result.getDisplay());
        assertEquals("Agents Rebooted", result.toString());
        assertEquals("AGENT_REBOOTED", result.name());
    }

    /**
     * Run the Map<String, String> getExtraContext() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetExtraContext_1()
            throws Exception {
        JobEvent fixture = new JobEvent("", "", JobLifecycleEvent.AGENT_REBOOTED);

        Map<String, String> result = fixture.getExtraContext();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the String getJobId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetJobId_1()
            throws Exception {
        JobEvent fixture = new JobEvent("", "", JobLifecycleEvent.AGENT_REBOOTED);

        String result = fixture.getJobId();

        assertEquals("", result);
    }

    /**
     * Run the String getMessage() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetMessage_1()
            throws Exception {
        JobEvent fixture = new JobEvent("", "", JobLifecycleEvent.AGENT_REBOOTED);

        String result = fixture.getMessage();

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
        JobEvent fixture = new JobEvent("", "", JobLifecycleEvent.AGENT_REBOOTED);

        String result = fixture.toString();

        assertNotNull(result);
    }
}