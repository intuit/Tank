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

import org.apache.lucene.search.Filter;

import org.junit.Assert;

import org.apache.lucene.search.Query;
import org.junit.*;

import static org.junit.Assert.*;

import org.testng.annotations.Test;

import com.intuit.tank.search.util.FieldSearchParam;
import com.intuit.tank.search.util.MultiSearchParam;
import com.intuit.tank.search.util.MustNotFieldSearchParam;
import com.intuit.tank.search.util.SearchParam;
import com.intuit.tank.search.util.SearchQuery;
import com.intuit.tank.search.util.SearchUtils;
import com.intuit.tank.search.util.MultiSearchParam.Operator;
import com.intuit.tank.search.util.SearchQuery.QueryBuilder;
import com.intuit.tank.test.TestGroups;

/**
 * SearchQueryTest
 * 
 * @author dangleton
 * 
 */
public class SearchQueryTest {

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testFieldQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        queryBuilder.addParam(new FieldSearchParam("fieldname", "valuetosearch"));
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        Assert.assertEquals(1, params.size());
        SearchParam param = params.iterator().next();
        String query = param.getQuery();
        Assert.assertEquals("fieldname:valuetosearch", query);
        String luceneString = param.getLuceneQuery().toString();
        Assert.assertEquals(query, luceneString);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMustNotFieldQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        queryBuilder.addParam(new MustNotFieldSearchParam("fieldname", "valuetosearch"));
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        Assert.assertEquals(1, params.size());
        SearchParam param = params.iterator().next();
        String query = param.getQuery();
        Assert.assertEquals("-fieldname:valuetosearch", query);
        String luceneString = param.getLuceneQuery().toString();
        Assert.assertEquals(query, luceneString);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMultiFieldQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        MultiSearchParam multiSearchParam = new MultiSearchParam(Operator.AND, new FieldSearchParam("fieldname",
                "valuetosearch"), new FieldSearchParam("fieldname2", "valuetosearch"));
        queryBuilder.addParam(multiSearchParam);
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        Assert.assertEquals(1, params.size());
        SearchParam param = params.iterator().next();
        String query = param.getQuery();
        Assert.assertEquals("(fieldname:valuetosearch AND fieldname2:valuetosearch)", query);
        String luceneString = param.getLuceneQuery().toString();
        Assert.assertEquals("+fieldname:valuetosearch +fieldname2:valuetosearch", luceneString);
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testMultipleQuery() throws Exception {
        QueryBuilder queryBuilder = SearchQuery.getBuilder();
        MultiSearchParam multiSearchParam = new MultiSearchParam(Operator.AND, new FieldSearchParam("FieldName",
                "valuetosearch"), new FieldSearchParam("FieldName2", "valuetosearch"));
        queryBuilder.addParam(multiSearchParam);
        queryBuilder.addParam(new MustNotFieldSearchParam("FieldName3", "valuetosearch"));
        Collection<SearchParam> params = queryBuilder.toSearchQuery().getSearchParams();
        Assert.assertEquals(2, params.size());
        Query query = SearchUtils.createLuceneQuery(Operator.AND, queryBuilder.toSearchQuery());
        String luceneString = query.toString();
        Assert.assertEquals("+(+FieldName:valuetosearch +FieldName2:valuetosearch) +(-FieldName3:valuetosearch)",
                luceneString);
    }

}
