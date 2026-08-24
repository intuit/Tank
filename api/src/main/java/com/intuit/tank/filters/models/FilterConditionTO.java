/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.filters.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "condition", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterCondition", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "scope",
        "condition",
        "value"
})
public class FilterConditionTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "scope", namespace = Namespace.NAMESPACE_V1)
    private String scope;

    @XmlElement(name = "condition", namespace = Namespace.NAMESPACE_V1)
    private String condition;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1)
    private String value;
}
