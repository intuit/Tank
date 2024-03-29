/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script.models;

import jakarta.xml.bind.annotation.*;
import lombok.Builder;

import java.io.Serializable;

@Builder(setterPrefix = "with", toBuilder = true)
@XmlRootElement(name = "stepData", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StepDataTO", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "key",
        "value",
        "type",
        "phase"
})
public class StepDataTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String key;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String value;

    @XmlElement(name = "type", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String type;

    @XmlElement(name = "phase", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String phase;

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the phase
     */
    public String getPhase() {
        return phase;
    }

    /**
     * @param phase
     *            the phase to set
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

}
