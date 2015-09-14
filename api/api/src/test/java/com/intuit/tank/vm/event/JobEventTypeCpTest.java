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

import org.junit.*;

import com.intuit.tank.vm.event.JobEventType;

import static org.junit.Assert.*;

/**
 * The class <code>JobEventTypeCpTest</code> contains tests for the class <code>{@link JobEventType}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class JobEventTypeCpTest {
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
        JobEventType fixture = JobEventType.AGENT_REBOOTED;

        String result = fixture.getDisplay();

        assertEquals("Agents Rebooted", result);
    }
}