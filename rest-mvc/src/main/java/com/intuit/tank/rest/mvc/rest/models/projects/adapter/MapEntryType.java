/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects.adapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class MapEntryType {

    @XmlElement(namespace = Namespace.NAMESPACE_V1)
    private String key;
    @XmlElement(namespace = Namespace.NAMESPACE_V1)
    private String value;

    public MapEntryType() {
    }

    public MapEntryType(Map.Entry<String, String> e) {
        key = e.getKey();
        value = e.getValue();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapEntryType)) {
            return false;
        }
        MapEntryType o = (MapEntryType) obj;
        return new EqualsBuilder().append(key, o.key).append(o.value, value).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(81, 91).append(key).append(value).toHashCode();
    }
}
