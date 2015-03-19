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

/**
 * FilterValues
 * 
 * @author dangleton
 * 
 */
public class TableViewState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> filteredValues = new HashMap<String, Object>();

    public Object getFilterValue(String key) {
        Object ret = filteredValues.get(key);
        if (ret == null) {
            ret = "";
            filteredValues.put(key, ret);
        }
        return ret;
    }

    public void setFilterValue(String key, Object value) {
        filteredValues.put(key, value);
    }

    /**
     * 
     */
    public void clearFilters() {
        filteredValues.clear();

    }

    public void onFilter(FilterEvent event) {
        Map<String, Object> filters = event.getFilters();
        clearFilters();
        for (Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            setFilterValue(key, value);
        }
    }

}
