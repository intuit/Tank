/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.filters.models;

import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder(setterPrefix = "with")
@XmlRootElement(name = "filter", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Filter", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "id",
        "name",
        "productName",
        "filterType",
        "allConditionsMustPass",
        "externalScriptId",
        "conditions",
        "actions"
})
public class FilterTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Integer id;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "productName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String productName;

    /** INTERNAL or EXTERNAL */
    @XmlElement(name = "filterType", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String filterType;

    @XmlElement(name = "allConditionsMustPass", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Boolean allConditionsMustPass;

    @XmlElement(name = "externalScriptId", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Integer externalScriptId;

    @XmlElementWrapper(name = "conditions", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "condition", namespace = Namespace.NAMESPACE_V1)
    private List<FilterConditionTO> conditions = new ArrayList<>();

    @XmlElementWrapper(name = "actions", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "action", namespace = Namespace.NAMESPACE_V1)
    private List<FilterActionTO> actions = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getFilterType() { return filterType; }
    public void setFilterType(String filterType) { this.filterType = filterType; }

    public Boolean getAllConditionsMustPass() { return allConditionsMustPass; }
    public void setAllConditionsMustPass(Boolean allConditionsMustPass) { this.allConditionsMustPass = allConditionsMustPass; }

    public Integer getExternalScriptId() { return externalScriptId; }
    public void setExternalScriptId(Integer externalScriptId) { this.externalScriptId = externalScriptId; }

    public List<FilterConditionTO> getConditions() { return conditions; }
    public void setConditions(List<FilterConditionTO> conditions) { this.conditions = conditions; }

    public List<FilterActionTO> getActions() { return actions; }
    public void setActions(List<FilterActionTO> actions) { this.actions = actions; }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
