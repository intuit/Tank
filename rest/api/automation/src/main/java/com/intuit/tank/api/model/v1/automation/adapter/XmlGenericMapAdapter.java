/*
 * Copyright 2011 John Yeary <jyeary@bluelotussoftware.com>.
 * Copyright 2011 Bluelotus Software, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: XmlGenericMapAdapter.java 399 2011-12-03 04:22:50Z jyeary $
 */
package com.intuit.tank.api.model.v1.automation.adapter;

/*
 * #%L
 * Automation Rest Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0
 */
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
