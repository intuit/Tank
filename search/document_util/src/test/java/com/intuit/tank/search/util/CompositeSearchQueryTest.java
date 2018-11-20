package com.intuit.tank.search.util;

/*
 * #%L
 * DocumentUtil
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

import com.intuit.tank.search.util.CompositeSearchQuery;
import com.intuit.tank.search.util.MultiSearchParam;
import com.intuit.tank.search.util.SearchQuery;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>CompositeSearchQueryTest</code> contains tests for the class
 * <code>{@link CompositeSearchQuery}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:27 AM
 */
public class CompositeSearchQueryTest {
    /**
     * Run the CompositeSearchQuery(SearchQuery,SearchQuery,Operator) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testCompositeSearchQuery_1()
            throws Exception {
        SearchQuery q1 = null;
        SearchQuery q2 = null;
        MultiSearchParam.Operator operator = MultiSearchParam.Operator.AND;

        CompositeSearchQuery result = new CompositeSearchQuery(q1, q2, operator);

        assertNotNull(result);
        assertEquals(null, result.getQuery2());
        assertEquals(null, result.getQuery1());
    }

    /**
     * Run the MultiSearchParam.Operator getOperator() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetOperator_1()
            throws Exception {
        CompositeSearchQuery fixture = new CompositeSearchQuery((SearchQuery) null, (SearchQuery) null,
                MultiSearchParam.Operator.AND);

        MultiSearchParam.Operator result = fixture.getOperator();

        assertNotNull(result);
        assertEquals("AND", result.getJoinTerm());
        assertEquals("AND", result.name());
        assertEquals("AND", result.toString());
        assertEquals(1, result.ordinal());
    }

    /**
     * Run the SearchQuery getQuery1() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetQuery1_1()
            throws Exception {
        CompositeSearchQuery fixture = new CompositeSearchQuery((SearchQuery) null, (SearchQuery) null,
                MultiSearchParam.Operator.AND);

        SearchQuery result = fixture.getQuery1();

        assertEquals(null, result);
    }

    /**
     * Run the SearchQuery getQuery2() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:27 AM
     */
    @Test
    public void testGetQuery2_1()
            throws Exception {
        CompositeSearchQuery fixture = new CompositeSearchQuery((SearchQuery) null, (SearchQuery) null,
                MultiSearchParam.Operator.AND);

        SearchQuery result = fixture.getQuery2();

        assertEquals(null, result);
    }
}