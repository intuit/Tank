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

/**
 * SortOrder
 */
public class SortOrder {

    private String field;
    private boolean descending;

    /**
     * 
     * @param field
     *            the field to sort on
     */
    public SortOrder(String field) {
        this(field, false);
    }

    /**
     * 
     * @param field
     *            the field to sort on
     * @param descending
     *            true if descending
     */
    public SortOrder(String field, boolean descending) {
        this.field = field;
        this.descending = descending;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @return the descending
     */
    public boolean isDescending() {
        return descending;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SortOrder)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SortOrder so = (SortOrder) obj;
        return new EqualsBuilder().append(field, so.field).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // hashcode for sort based on field and not ascending or descending
        return new HashCodeBuilder(49, 15).append(field).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return field + (descending ? "descending" : "ascending");
    }

}
