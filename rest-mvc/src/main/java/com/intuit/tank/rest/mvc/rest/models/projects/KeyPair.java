/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "key-pair", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyPair", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "key",
        "value"
})
public class KeyPair implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String key;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String value;

    public KeyPair() {
        super();
    }

    public KeyPair(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

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

    @Override
    public String toString() {
        return key + " = " + value;
    }

}
