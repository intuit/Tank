/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapType {

    private List<MapEntryType> entry = new ArrayList<MapEntryType>();

    /**
     * 
     */
    public MapType() {
    }

    /**
     * 
     * @param map
     */
    public MapType(Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            entry.add(new MapEntryType(e));
        }
    }

    /**
     * 
     * @return
     */
    public List<MapEntryType> getEntry() {
        return entry;
    }

    /**
     * 
     * @param entry
     */
    public void setEntry(List<MapEntryType> entry) {
        this.entry = entry;
    }

}
