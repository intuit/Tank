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

import com.intuit.tank.search.util.MultiSearchParam.Operator;

public class CompositeSearchQuery {

    private Operator operator;
    private SearchQuery query1;
    private SearchQuery query2;

    public CompositeSearchQuery(SearchQuery q1, SearchQuery q2, Operator operator) {
        this.operator = operator;
        this.query1 = q1;
        this.query2 = q2;
    }

    /**
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @return the query1
     */
    public SearchQuery getQuery1() {
        return query1;
    }

    /**
     * @return the query2
     */
    public SearchQuery getQuery2() {
        return query2;
    }

}
