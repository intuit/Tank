/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.projects.models;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "automationTestPlan", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationTestPlan", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "name",
        "user_percentage",
        "position",
        "script_groups"
})
public class AutomationTestPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String name;

    @Builder.Default
    @XmlElement(name = "user_percentage", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int userPercentage = 100;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer position;

    @Singular(ignoreNullCollections = true)
    @XmlElement(name = "script_groups", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<AutomationScriptGroup> scriptGroups;

    /**
     * @return the name
     */
    public String getName() { return name; }

    /**
     * @return the userPercentage
     */
    public int getUserPercentage() {
        return userPercentage;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the scriptGroups
     */
    public List<AutomationScriptGroup> getScriptGroups() {
        return scriptGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("userPercentage", userPercentage).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AutomationTestPlan)) {
            return false;
        }
        AutomationTestPlan o = (AutomationTestPlan) obj;
        return new EqualsBuilder().append(o.name, name).append(o.userPercentage, userPercentage).append(o.position, position)
                .append(o.scriptGroups, scriptGroups).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 45).append(getName()).append(getUserPercentage()).append(getPosition()).toHashCode();
    }

}
