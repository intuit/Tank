/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.filters.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "filterAction", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterActionTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** remove | replace | add */
    @XmlElement(name = "action", namespace = Namespace.NAMESPACE_V1)
    private String action;

    @XmlElement(name = "scope", namespace = Namespace.NAMESPACE_V1)
    private String scope;

    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1)
    private String key;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1)
    private String value;
}
