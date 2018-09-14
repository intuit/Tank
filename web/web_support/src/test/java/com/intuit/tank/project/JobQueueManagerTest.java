package com.intuit.tank.project;

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

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.JobQueueManager;

/**
 * The class <code>JobQueueManagerTest</code> contains tests for the class <code>{@link JobQueueManager}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class JobQueueManagerTest {
    /**
     * Run the JobQueueManager() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testJobQueueManager_1()
        throws Exception {
        JobQueueManager result = new JobQueueManager();
        assertNotNull(result);
    }

    /**
     * Run the Integer getRootJobId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetRootJobId_1()
        throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        Integer result = fixture.getRootJobId();
        assertNull(result);
    }
}