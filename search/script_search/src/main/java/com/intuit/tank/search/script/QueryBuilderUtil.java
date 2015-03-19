package com.intuit.tank.search.script;

/*
 * #%L
 * Script Search
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import org.apache.lucene.search.Query;

import com.intuit.tank.search.util.CompositeSearchQuery;
import com.intuit.tank.search.util.FieldSearchParam;
import com.intuit.tank.search.util.SearchQuery;
import com.intuit.tank.search.util.SearchUtils;
import com.intuit.tank.search.util.MultiSearchParam.Operator;
import com.intuit.tank.search.util.SearchQuery.QueryBuilder;

public class QueryBuilderUtil {

    public Query build(String scriptId, String searchQuery, SearchCriteria criteria) {
        QueryBuilder idQueryBuilder = SearchQuery.getBuilder();
        idQueryBuilder.addParam(new FieldSearchParam(ScriptSearchField.scriptId.getValue(), scriptId));
        QueryBuilder actualQueryBuilder = SearchQuery.getBuilder();
        List<Section> criteria2 = criteria.getCriteria();
        for (Section section : criteria2) {
            actualQueryBuilder.addParam(new FieldSearchParam(section.toString(), searchQuery));
        }
        CompositeSearchQuery csq = new CompositeSearchQuery(idQueryBuilder.toSearchQuery(),
                actualQueryBuilder.toSearchQuery(),
                Operator.AND);
        Query createLuceneQuery = SearchUtils.createLuceneQuery(Operator.AND, csq);
        return createLuceneQuery;
    }

}
