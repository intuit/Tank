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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "filterGroup", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterGroup", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "id",
        "created",
        "modified",
        "creator",
        "name",
        "productName",
        "filterIds"
})
public class FilterGroupTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Integer id;

    @XmlElement(name = "created", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date created;

    @XmlElement(name = "modified", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date modified;

    @XmlElement(name = "creator", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String creator;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "productName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String productName;

    @XmlElementWrapper(name = "filterIds", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "filterId", namespace = Namespace.NAMESPACE_V1)
    private List<Integer> filterIds;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
