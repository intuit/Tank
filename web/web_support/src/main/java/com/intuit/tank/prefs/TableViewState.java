/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.prefs;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.FilterMeta;

/**
 * FilterValues
 * 
 * @author dangleton
 * 
 */
public class TableViewState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, FilterMeta> filteredValues = new HashMap<String, FilterMeta>();

    public Object getFilterValue(String key) {
        return filteredValues.computeIfAbsent(key, k -> new FilterMeta());
    }

    public void setFilterValue(String key, FilterMeta value) {
        filteredValues.put(key, value);
    }

    /**
     * 
     */
    public void clearFilters() {
        filteredValues.clear();

    }

    public void onFilter(FilterEvent event) {
        Map<String, FilterMeta> filters = event.getFilterBy();
        clearFilters();
        for (Entry<String, FilterMeta> entry : filters.entrySet()) {
            setFilterValue(entry.getKey(), entry.getValue());
        }
    }

}
