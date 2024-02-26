/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class XmlGenericMapAdapter extends XmlAdapter<MapType, Map<String, String>> {

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> unmarshal(MapType v) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        if (v != null) {
            for (MapEntryType mapEntryType : v.getEntry()) {
                map.put(mapEntryType.getKey(), mapEntryType.getValue());
            }
        }
        return map;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public MapType marshal(Map<String, String> v) throws Exception {
        MapType mapType = new MapType();

        if (v != null) {
            for (Map.Entry<String, String> entry : v.entrySet()) {
                MapEntryType mapEntryType = new MapEntryType();
                mapEntryType.setKey(entry.getKey());
                mapEntryType.setValue(entry.getValue());
                mapType.getEntry().add(mapEntryType);
            }
        }
        return mapType;
    }
}
