package com.intuit.tank.vm.vmManager;

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

import java.util.Set;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.vmManager.JobRequestImpl;

/**
 * The class <code>JobRequestImplCpTest</code> contains tests for the class <code>{@link JobRequestImpl}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class JobRequestImplCpTest {
    /**
     * Run the JobRequestImpl.Builder builder() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testBuilder_1()
            throws Exception {

        JobRequestImpl.Builder result = JobRequestImpl.builder();

        assertNotNull(result);
    }
}