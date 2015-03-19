package com.intuit.tank.reporting.databases;

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

import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.reporting.databases.PagedDatabaseResult;

import static org.junit.Assert.*;

/**
 * The class <code>PagedDatabaseResultTest</code> contains tests for the class <code>{@link PagedDatabaseResult}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:30 AM
 */
public class PagedDatabaseResultTest {
    /**
     * Run the PagedDatabaseResult(List<Item>,Object) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testPagedDatabaseResult_1()
            throws Exception {
        List<Item> items = new LinkedList();
        Object nextToken = new Object();

        PagedDatabaseResult result = new PagedDatabaseResult(items, nextToken);

        assertNotNull(result);
    }

    /**
     * Run the List<Item> getItems() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testGetItems_1()
            throws Exception {
        PagedDatabaseResult fixture = new PagedDatabaseResult(new LinkedList(), new Object());

        List<Item> result = fixture.getItems();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the Object getNextToken() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:30 AM
     */
    @Test
    public void testGetNextToken_1()
            throws Exception {
        PagedDatabaseResult fixture = new PagedDatabaseResult(new LinkedList(), new Object());

        Object result = fixture.getNextToken();

        assertNotNull(result);
    }
}