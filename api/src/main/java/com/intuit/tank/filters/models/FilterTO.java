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
@XmlRootElement(name = "filter", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Filter", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "id",
        "created",
        "modified",
        "creator",
        "name",
        "productName",
        "persist",
        "allConditionsMustPass",
        "filterType",
        "externalScriptId",
        "conditions",
        "actions"
})
public class FilterTO implements Serializable {

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

    @XmlElement(name = "persist", namespace = Namespace.NAMESPACE_V1)
    @Builder.Default
    private boolean persist = true;

    @XmlElement(name = "allConditionsMustPass", namespace = Namespace.NAMESPACE_V1)
    private boolean allConditionsMustPass;

    @XmlElement(name = "filterType", namespace = Namespace.NAMESPACE_V1)
    @Builder.Default
    private String filterType = "INTERNAL";

    @XmlElement(name = "externalScriptId", namespace = Namespace.NAMESPACE_V1)
    private Integer externalScriptId;

    @XmlElementWrapper(name = "conditions", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "condition", namespace = Namespace.NAMESPACE_V1)
    private List<FilterConditionTO> conditions;

    @XmlElementWrapper(name = "actions", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "action", namespace = Namespace.NAMESPACE_V1)
    private List<FilterActionTO> actions;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
