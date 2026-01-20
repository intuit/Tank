package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Specifies which occurrence to extract when saving WebSocket message content to a variable.
 */
@XmlType(name = "saveOccurrence", namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlEnum(String.class)
public enum SaveOccurrence {

    @XmlEnumValue("first")
    FIRST("first"),
    
    @XmlEnumValue("last")
    LAST("last");

    private final String value;

    SaveOccurrence(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SaveOccurrence fromValue(String value) {
        if (value == null) {
            return LAST;  // Default to last occurrence
        }
        
        String normalizedValue = value.toLowerCase().trim();
        for (SaveOccurrence occurrence : SaveOccurrence.values()) {
            if (occurrence.value.equals(normalizedValue)) {
                return occurrence;
            }
        }
        
        throw new IllegalArgumentException("Unknown save occurrence: " + value + 
            ". Valid values are: first, last");
    }

    @Override
    public String toString() {
        return value;
    }
}
