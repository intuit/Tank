/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.filters.models;

import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
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
@XmlRootElement(name = "action", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterAction", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "action",
        "scope",
        "key",
        "value"
})
public class FilterActionTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "action", namespace = Namespace.NAMESPACE_V1)
    private ScriptFilterActionType action;

    @XmlElement(name = "scope", namespace = Namespace.NAMESPACE_V1)
    private String scope;

    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1)
    private String key;

    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1)
    private String value;
}
