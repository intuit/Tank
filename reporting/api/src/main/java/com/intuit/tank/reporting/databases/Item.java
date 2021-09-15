/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.reporting.databases;

/*
 * #%L
 * Reporting API
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
import java.util.List;

/**
 * Item
 * 
 * @author dangleton
 * 
 */

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Attribute> attributes;

    /**
     * @param name
     * @param attributes
     */
    public Item(String name, List<Attribute> attributes) {
        super();
        this.name = name;
        this.attributes = attributes;
    }

    /**
     * 
     */
    public Item() {
        super();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes
     *            the attributes to set
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Item) {
            return name.equals(((Item) obj).name);
        }
        return false;
    }
}
