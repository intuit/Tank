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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * FieldSearchParam
 */
public class MustNotFieldSearchParam implements SearchParam {

    private String fieldName;
    private String value;

    /**
     * 
     * @param fieldName
     *            the field to serch on
     * @param value
     *            the value to search
     */
    public MustNotFieldSearchParam(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getQuery() {
        return "-" + fieldName + ":" + value + "";
    }

    /**
     * {@inheritDoc}
     */
    public Query getLuceneQuery() {
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(new TermQuery(new Term(fieldName, value)), Occur.MUST_NOT);
        return booleanQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MustNotFieldSearchParam)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        MustNotFieldSearchParam so = (MustNotFieldSearchParam) obj;
        return new EqualsBuilder().append(fieldName, so.fieldName).append(value, so.value).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(45, 11).append(fieldName).append(value).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getQuery();
    }

}
