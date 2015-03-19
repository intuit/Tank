package com.intuit.tank.vm.api.enumerated;

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

import org.junit.*;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;

import static org.junit.Assert.*;

/**
 * The class <code>JobLifecycleEventCpTest</code> contains tests for the class <code>{@link JobLifecycleEvent}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class JobLifecycleEventCpTest {
    /**
     * Run the String getDisplay() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetDisplay_1()
            throws Exception {
        JobLifecycleEvent fixture = JobLifecycleEvent.AGENT_REBOOTED;

        String result = fixture.getDisplay();

        assertEquals("Agents Rebooted", result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        JobLifecycleEvent fixture = JobLifecycleEvent.AGENT_REBOOTED;

        String result = fixture.toString();

        assertEquals("Agents Rebooted", result);
    }
}