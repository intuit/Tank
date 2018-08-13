package com.intuit.tank.results;

/*
 * #%L
 * Reporting API
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

import com.intuit.tank.results.CreateTableMessage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>CreateTableMessageTest</code> contains tests for the class <code>{@link CreateTableMessage}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:31 AM
 */
public class CreateTableMessageTest {
    /**
     * Run the CreateTableMessage() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testCreateTableMessage_1()
            throws Exception {
        CreateTableMessage result = new CreateTableMessage();
        assertNotNull(result);
    }

    /**
     * Run the String getTableName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetTableName_1()
            throws Exception {
        CreateTableMessage fixture = new CreateTableMessage();
        fixture.setTableName("");

        String result = fixture.getTableName();

        assertEquals("", result);
    }

    /**
     * Run the void setTableName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testSetTableName_1()
            throws Exception {
        CreateTableMessage fixture = new CreateTableMessage();
        fixture.setTableName("");
        String tableName = "";

        fixture.setTableName(tableName);

    }
}