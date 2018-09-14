package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.perfManager.workLoads.IncreasingWorkLoad;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.VMChannel;

/**
 * The class <code>IncreasingWorkLoadTest</code> contains tests for the class <code>{@link IncreasingWorkLoad}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class IncreasingWorkLoadTest {
    /**
     * Run the IncreasingWorkLoad(VMChannel,AgentDispatcher,JobRequest) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testIncreasingWorkLoad_1()
            throws Exception {
        VMChannel channel = null;
        AgentDispatcher agentDispatcher = null;
        JobRequest job = null;

        IncreasingWorkLoad result = new IncreasingWorkLoad(channel, agentDispatcher, job);

        assertNotNull(result);
        assertEquals(null, result.getJob());
    }

    /**
     * Run the JobRequest getJob() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testGetJob_1()
            throws Exception {
        IncreasingWorkLoad fixture = new IncreasingWorkLoad((VMChannel) null, (AgentDispatcher) null, (JobRequest) null);

        JobRequest result = fixture.getJob();

        assertEquals(null, result);
    }

}