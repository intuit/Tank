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
import org.apache.lucene.search.Query;

/**
 * LuceneSearchParam
 */
public class LuceneSearchParam implements SearchParam {

    private Query query;

    /**
     * 
     * @param query
     *            the lucene query to specify
     */
    public LuceneSearchParam(Query query) {
        this.query = query;
    }

    /**
     * {@inheritDoc}
     */
    public String getQuery() {
        return query.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Query getLuceneQuery() {
        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LuceneSearchParam)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        LuceneSearchParam so = (LuceneSearchParam) obj;
        return new EqualsBuilder().append(query, so.query).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(43, 19).append(query).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getQuery();
    }

}
