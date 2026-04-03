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

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>JobQueueStatusCpTest</code> contains tests for the class <code>{@link JobQueueStatus}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class JobQueueStatusCpTest {

    @Test
    void testDeletedStatusExists() {
        JobQueueStatus deleted = JobQueueStatus.valueOf("Deleted");
        assertNotNull(deleted);
        assertEquals("Deleted", deleted.name());
    }

    @Test
    void testAllExpectedValuesPresent() {
        JobQueueStatus[] values = JobQueueStatus.values();
        assertEquals(10, values.length);
        assertEquals(JobQueueStatus.Deleted, values[values.length - 1]);
    }
}