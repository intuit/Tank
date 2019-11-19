/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.util.Collection;

import org.apache.lucene.search.Query;

import com.intuit.tank.search.util.MultiSearchParam.Operator;
import com.intuit.tank.search.util.SearchQuery.QueryBuilder;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SearchQueryTest
 * 
 * @author dangleton
 * 
 */
public class SearchQueryTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testFieldQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        queryBuilder.addParam(new FieldSearchParam("fieldname", "valuetosearch"));
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        assertEquals(1, params.size());
        SearchParam param = params.iterator().next();
        String query = param.getQuery();
        assertEquals("fieldname:valuetosearch", query);
        String luceneString = param.getLuceneQuery().toString();
        assertEquals(query, luceneString);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMustNotFieldQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        queryBuilder.addParam(new MustNotFieldSearchParam("fieldname", "valuetosearch"));
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        assertEquals(1, params.size());
        SearchParam param = params.iterator().next();
        String query = param.getQuery();
        assertEquals("-fieldname:valuetosearch", query);
        String luceneString = param.getLuceneQuery().toString();
        assertEquals(query, luceneString);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMultiFieldQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        MultiSearchParam multiSearchParam = new MultiSearchParam(Operator.AND, new FieldSearchParam("fieldname",
                "valuetosearch"), new FieldSearchParam("fieldname2", "valuetosearch"));
        queryBuilder.addParam(multiSearchParam);
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        assertEquals(1, params.size());
        SearchParam param = params.iterator().next();
        String query = param.getQuery();
        assertEquals("(fieldname:valuetosearch AND fieldname2:valuetosearch)", query);
        String luceneString = param.getLuceneQuery().toString();
        assertEquals("+fieldname:valuetosearch +fieldname2:valuetosearch", luceneString);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testMultipleQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        MultiSearchParam multiSearchParam = new MultiSearchParam(Operator.AND, new FieldSearchParam("FieldName",
                "valuetosearch"), new FieldSearchParam("FieldName2", "valuetosearch"));
        queryBuilder.addParam(multiSearchParam);
        queryBuilder.addParam(new MustNotFieldSearchParam("FieldName3", "valuetosearch"));
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        assertEquals(2, params.size());
        Query query = SearchUtils.createLuceneQuery(Operator.AND, queryBuilder.toSearchQuery());
        String luceneString = query.toString();
        assertEquals("+(+FieldName:valuetosearch +FieldName2:valuetosearch) +(-FieldName3:valuetosearch)",
                luceneString);
    }

}
