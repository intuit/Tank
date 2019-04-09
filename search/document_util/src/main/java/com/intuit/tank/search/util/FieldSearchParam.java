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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

/**
 * FieldSearchParam
 * 
 */
public class FieldSearchParam implements SearchParam {

    private String fieldName;
    private String value;

    /**
     * 
     * @param fieldName
     *            the field to serch on
     * @param value
     *            the value to search
     */
    public FieldSearchParam(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getQuery() {
        return fieldName + ":" + value + "";
    }

    /**
     * {@inheritDoc}
     */
    public Query getLuceneQuery() {
        QueryParser queryParser = new QueryParser(fieldName, SearchConstants.analyzer);
        if (value.startsWith("*")) {
            queryParser.setAllowLeadingWildcard(true);
        }
        try {
            return queryParser.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
        // return new TermQuery(new Term(fieldName, value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FieldSearchParam)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        FieldSearchParam so = (FieldSearchParam) obj;
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
