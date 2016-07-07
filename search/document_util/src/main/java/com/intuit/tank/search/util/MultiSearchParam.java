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

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * MultiSearchParam
 * 
 */
public class MultiSearchParam implements SearchParam {
    /**
     */
    public enum Operator {
        OR("OR", Occur.SHOULD),
        AND("AND", Occur.MUST),
        NOT("AND NOT", Occur.MUST_NOT);
        private String joinTerm;
        private Occur occur;

        Operator(String joinTerm, Occur occur) {
            this.joinTerm = joinTerm;
            this.occur = occur;
        }

        public Occur getOccur() {
            return occur;
        }

        public String getJoinTerm() {
            return joinTerm;
        }
    }

    private Operator operator;
    private SearchParam[] params;

    /**
     * 
     * @param operator
     *            the operator to specify
     * @param params
     *            must be at least one param
     */
    public MultiSearchParam(Operator operator, SearchParam... params) {
        this.operator = operator;
        this.params = Arrays.copyOf(params, params.length);
        assert params.length != 0;
    }

    /**
     * {@inheritDoc}
     */
    public String getQuery() {
        StringBuilder sb = new StringBuilder();
        if (params.length != 1) {
            sb.append('(');
        }
        for (SearchParam param : params) {
            if (sb.length() > 1) {
                sb.append(' ').append(operator.joinTerm).append(' ');
            }
            sb.append(param.getQuery());
        }
        if (params.length != 1) {
            sb.append(')');
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Query getLuceneQuery() {
        BooleanQuery ret = new BooleanQuery();
        for (SearchParam param : params) {
            if (param != null) {
                ret.add(new BooleanClause(param.getLuceneQuery(), operator.occur));
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiSearchParam)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MultiSearchParam so = (MultiSearchParam) obj;
        return new EqualsBuilder().append(operator, so.operator).append(params, so.params).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(43, 19).append(operator).append(params).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getQuery();
    }

}
