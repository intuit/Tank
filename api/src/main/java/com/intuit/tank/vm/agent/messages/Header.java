package com.intuit.tank.vm.agent.messages;

/*
 * #%L
 * Intuit Tank Api
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "header", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Header", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "key",
        "value"
})
public class Header implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String key;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String value;

    public Header() {
        super();
    }

    public Header(String key, String value) {
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
