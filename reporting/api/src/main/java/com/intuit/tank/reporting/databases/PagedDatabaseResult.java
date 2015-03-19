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
 * PagedDatabaseResult
 * 
 * @author dangleton
 * 
 */
public class PagedDatabaseResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Item> items;
    private Object nextToken;

    /**
     * @param items
     * @param nextToken
     */
    public PagedDatabaseResult(List<Item> items, Object nextToken) {
        super();
        this.items = items;
        this.nextToken = nextToken;
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @return the nextToken
     */
    public Object getNextToken() {
        return nextToken;
    }

}
