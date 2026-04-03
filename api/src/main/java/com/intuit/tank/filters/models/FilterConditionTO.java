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
@XmlRootElement(name = "filterCondition", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterConditionTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "scope", namespace = Namespace.NAMESPACE_V1)
    private String scope;

    @XmlElement(name = "condition", namespace = Namespace.NAMESPACE_V1)
    private String condition;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1)
    private String value;
}
