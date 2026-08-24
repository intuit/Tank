/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.filters.models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "filterGroup", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterGroupDetail", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "filters"
})
public class FilterGroupDetailTO extends FilterGroupTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "filters", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "filter", namespace = Namespace.NAMESPACE_V1)
    private List<FilterTO> filters;
}
