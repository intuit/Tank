/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.project;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * VariableEntry
 * 
 * @author dangleton
 * 
 */
public class VariableEntry implements Serializable, Comparable<VariableEntry> {

    private static final long serialVersionUID = 1L;
    private String key;
    private String value;

    /**
     * 
     */
    public VariableEntry() {
        super();
    }

    /**
     * @param key
     * @param value
     */
    public VariableEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(15, 31).append(key).append(value).toHashCode();
    }

    @Override
    public boolean equals(Object arg) {
        if (arg == null || !(arg instanceof VariableEntry)) {
            return false;
        }
        VariableEntry o = (VariableEntry) arg;
        return new EqualsBuilder().append(key, o.key).append(value, o.value).isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return key + "= " + value;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int compareTo(VariableEntry o) {
        return this.key.compareTo(o.key);
    }

}
