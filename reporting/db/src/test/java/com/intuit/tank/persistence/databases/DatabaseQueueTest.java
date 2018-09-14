package com.intuit.tank.persistence.databases;

/*
 * #%L
 * Reporting database support
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

import com.intuit.tank.persistence.databases.DatabaseQueue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>DatabaseQueueTest</code> contains tests for the class <code>{@link DatabaseQueue}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:32 AM
 */
public class DatabaseQueueTest {
    /**
     * Run the DatabaseQueue clone() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test()
    public void testClone_1()
            throws Exception {
        DatabaseQueue fixture = DatabaseQueue.getInstance();

        assertThrows(java.lang.CloneNotSupportedException.class, () -> fixture.clone());
    }

    /**
     * Run the void execute(Runnable) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testExecute_1()
            throws Exception {
        DatabaseQueue fixture = DatabaseQueue.getInstance();
        Runnable runnable = new Thread();

        fixture.execute(runnable);

    }

    /**
     * Run the DatabaseQueue getInstance() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:32 AM
     */
    @Test
    public void testGetInstance_1()
            throws Exception {

        DatabaseQueue result = DatabaseQueue.getInstance();

        assertNotNull(result);
    }
}